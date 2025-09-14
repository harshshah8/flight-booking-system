package com.fbs.booking.service;

import com.fbs.booking.accessor.InventoryServiceAccessor;
import com.fbs.booking.accessor.PaymentServiceAccessor;
import com.fbs.booking.dto.*;
import com.fbs.booking.entity.Booking;
import com.fbs.booking.entity.BookingStatus;
import com.fbs.booking.exception.BookingServiceError;
import com.fbs.booking.exception.BookingServiceException;
import com.fbs.booking.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private InventoryServiceAccessor inventoryServiceAccessor;

    @Autowired
    private PaymentServiceAccessor paymentServiceAccessor;

    /**
     * Create booking with atomic seat blocking mechanism
     */
    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        logger.info("Starting booking process for flight: {}, seats: {}, customer: {}",
                   request.getFlightId(), request.getNumberOfSeats(), request.getCustomerEmail());

        // Step 1: Eager cleanup of expired bookings
        //TODO :TECH DEBT: This is lazy clean up. Implement CRON which will run in background and do the clean up in next phase
        cleanupExpiredBookings();

        // Step 2: Get flight details and calculate total amount
        FlightDto flight = inventoryServiceAccessor.getFlightById(request.getFlightId());
        if (flight == null) {
            throw new BookingServiceException(BookingServiceError.FLIGHT_NOT_FOUND);
        }

        BigDecimal totalAmount = BigDecimal.valueOf(flight.getCost()).multiply(BigDecimal.valueOf(request.getNumberOfSeats()));

        // Step 3: Create initial booking record (INITIATED status)
        Booking booking = new Booking(
            request.getFlightId(),
            request.getCustomerEmail(),
            request.getNumberOfSeats(),
            BookingStatus.INITIATED,
            totalAmount
        );
        bookingRepository.save(booking);

        logger.info("Booking created with ID: {}, status: INITIATED", booking.getId());

        // Step 4: Atomic seat reservation
        boolean seatsReserved = inventoryServiceAccessor.reserveSeats(request.getFlightId(), request.getNumberOfSeats());

        if (!seatsReserved) {
            // Insufficient seats - mark booking as failed
            booking.setBookingStatus(BookingStatus.FAILED);
            bookingRepository.save(booking);

            logger.warn("Booking failed - insufficient seats. Booking ID: {}", booking.getId());
            return BookingResponse.builder()
                    .bookingId(booking.getId())
                    .flightId(booking.getFlightId())
                    .customerEmail(booking.getCustomerEmail())
                    .numberOfSeats(booking.getNumberOfSeats())
                    .status(BookingStatus.FAILED)
                    .totalAmount(booking.getTotalAmount())
                    .failureReason("Insufficient seats available")
                    .createdAt(booking.getCreatedAt())
                    .updatedAt(booking.getUpdatedAt())
                    .build();
        }

        // Step 5: Seats successfully reserved - update booking status
        booking.setBookingStatus(BookingStatus.SEATS_RESERVED);
        bookingRepository.save(booking);

        logger.info("Seats reserved successfully. Booking ID: {}, status: SEATS_RESERVED", booking.getId());

        // Step 6: Process payment
        PaymentRequest paymentRequest = new PaymentRequest(
            booking.getId(),
            totalAmount,
            "USD",
            request.getCustomerEmail()
        );

        try {
            PaymentResponse paymentResponse = paymentServiceAccessor.processPayment(paymentRequest);

            if (paymentResponse.getStatus() == PaymentResponse.PaymentStatus.SUCCESS) {
                // Payment successful - confirm booking
                booking.setBookingStatus(BookingStatus.CONFIRMED);
                booking.setPaymentId(paymentResponse.getPaymentId());
                bookingRepository.save(booking);

                logger.info("Booking confirmed successfully. Booking ID: {}, Payment ID: {}",
                           booking.getId(), paymentResponse.getPaymentId());

                return BookingResponse.builder()
                        .bookingId(booking.getId())
                        .flightId(booking.getFlightId())
                        .customerEmail(booking.getCustomerEmail())
                        .numberOfSeats(booking.getNumberOfSeats())
                        .status(BookingStatus.CONFIRMED)
                        .totalAmount(booking.getTotalAmount())
                        .paymentId(paymentResponse.getPaymentId())
                        .createdAt(booking.getCreatedAt())
                        .updatedAt(booking.getUpdatedAt())
                        .build();

            } else {
                // Payment failed - rollback seats and mark booking as failed
                inventoryServiceAccessor.releaseSeats(request.getFlightId(), request.getNumberOfSeats());
                booking.setBookingStatus(BookingStatus.FAILED);
                bookingRepository.save(booking);

                logger.warn("Booking failed - payment declined. Booking ID: {}, Reason: {}",
                           booking.getId(), paymentResponse.getFailureReason());

                return BookingResponse.builder()
                        .bookingId(booking.getId())
                        .flightId(booking.getFlightId())
                        .customerEmail(booking.getCustomerEmail())
                        .numberOfSeats(booking.getNumberOfSeats())
                        .status(BookingStatus.FAILED)
                        .totalAmount(booking.getTotalAmount())
                        .failureReason("Payment failed: " + paymentResponse.getFailureReason())
                        .createdAt(booking.getCreatedAt())
                        .updatedAt(booking.getUpdatedAt())
                        .build();
            }

        } catch (Exception e) {
            // Payment service error - rollback seats and mark booking as failed
            inventoryServiceAccessor.releaseSeats(request.getFlightId(), request.getNumberOfSeats());
            booking.setBookingStatus(BookingStatus.FAILED);
            bookingRepository.save(booking);

            logger.error("Booking failed - payment service error. Booking ID: {}", booking.getId(), e);

            return BookingResponse.builder()
                    .bookingId(booking.getId())
                    .flightId(booking.getFlightId())
                    .customerEmail(booking.getCustomerEmail())
                    .numberOfSeats(booking.getNumberOfSeats())
                    .status(BookingStatus.FAILED)
                    .totalAmount(booking.getTotalAmount())
                    .failureReason("Payment processing error")
                    .createdAt(booking.getCreatedAt())
                    .updatedAt(booking.getUpdatedAt())
                    .build();
        }
    }

    /**
     * Eager cleanup of expired bookings (timeout handling)
     */
    public void cleanupExpiredBookings() {
        LocalDateTime expiredBefore = LocalDateTime.now().minusMinutes(5); // 5-minute timeout
        List<Booking> expiredBookings = bookingRepository.findExpiredReservations(BookingStatus.SEATS_RESERVED, expiredBefore);

        for (Booking expiredBooking : expiredBookings) {
            logger.info("Cleaning up expired booking: {}", expiredBooking.getId());

            // Release seats
            inventoryServiceAccessor.releaseSeats(expiredBooking.getFlightId(), expiredBooking.getNumberOfSeats());

            // Mark booking as expired
            expiredBooking.setBookingStatus(BookingStatus.EXPIRED);
            bookingRepository.save(expiredBooking);

            logger.info("Expired booking cleaned up: {}", expiredBooking.getId());
        }

        if (!expiredBookings.isEmpty()) {
            logger.info("Cleaned up {} expired bookings", expiredBookings.size());
        }
    }

    /**
     * Get booking by ID
     */
    public BookingResponse getBookingById(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            throw new BookingServiceException(BookingServiceError.BOOKING_NOT_FOUND);
        }

        return BookingResponse.builder()
                .bookingId(booking.getId())
                .flightId(booking.getFlightId())
                .customerEmail(booking.getCustomerEmail())
                .numberOfSeats(booking.getNumberOfSeats())
                .status(booking.getBookingStatus())
                .totalAmount(booking.getTotalAmount())
                .paymentId(booking.getPaymentId())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }

    /**
     * Get bookings by customer email
     */
    public List<BookingResponse> getBookingsByCustomerEmail(String customerEmail) {
        List<Booking> bookings = bookingRepository.findByCustomerEmailOrderByCreatedAtDesc(customerEmail);

        return bookings.stream()
                .map(booking -> BookingResponse.builder()
                        .bookingId(booking.getId())
                        .flightId(booking.getFlightId())
                        .customerEmail(booking.getCustomerEmail())
                        .numberOfSeats(booking.getNumberOfSeats())
                        .status(booking.getBookingStatus())
                        .totalAmount(booking.getTotalAmount())
                        .paymentId(booking.getPaymentId())
                        .createdAt(booking.getCreatedAt())
                        .updatedAt(booking.getUpdatedAt())
                        .build())
                .toList();
    }
}