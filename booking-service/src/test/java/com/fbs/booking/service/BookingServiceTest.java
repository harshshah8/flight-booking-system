package com.fbs.booking.service;

import com.fbs.booking.accessor.InventoryServiceAccessor;
import com.fbs.booking.accessor.PaymentServiceAccessor;
import com.fbs.booking.config.BookingConfig;
import com.fbs.booking.dto.BookingRequest;
import com.fbs.booking.dto.BookingResponse;
import com.fbs.booking.dto.PaymentResponse;
import com.fbs.booking.dto.FlightDto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fbs.booking.entity.Booking;
import com.fbs.booking.entity.BookingStatus;
import com.fbs.booking.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private InventoryServiceAccessor inventoryServiceAccessor;

    @Mock
    private PaymentServiceAccessor paymentServiceAccessor;

    @Mock
    private BookingConfig bookingConfig;

    @InjectMocks
    private BookingService bookingService;

    private BookingRequest validRequest;
    private UUID flightId;

    @BeforeEach
    void setUp() {
        flightId = UUID.randomUUID();
        validRequest = new BookingRequest(flightId, "test@example.com", 2);

        BookingConfig.Timeout timeout = new BookingConfig.Timeout();
        timeout.setSeatReservationMinutes(5);
        when(bookingConfig.getTimeout()).thenReturn(timeout);

        // Mock flight data
        FlightDto mockFlight = new FlightDto();
        mockFlight.setFlightId(flightId);
        mockFlight.setCost(299.99);
        mockFlight.setAvailableSeats(100);
        mockFlight.setBookedSeats(20);
        when(inventoryServiceAccessor.getFlightById(flightId)).thenReturn(mockFlight);
    }

    @Test
    void createBooking_Success() {
        // Arrange
        when(inventoryServiceAccessor.reserveSeats(flightId, 2)).thenReturn(true);
        when(paymentServiceAccessor.processPayment(any())).thenReturn(createSuccessfulPaymentResponse());
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setId(UUID.randomUUID());
            return booking;
        });

        // Act
        BookingResponse response = bookingService.createBooking(validRequest);

        // Assert
        assertEquals(BookingStatus.CONFIRMED, response.getStatus());
        assertEquals("test@example.com", response.getCustomerEmail());
        assertEquals(2, response.getNumberOfSeats());
        assertNull(response.getFailureReason());
        verify(inventoryServiceAccessor).reserveSeats(flightId, 2);
        verify(paymentServiceAccessor).processPayment(any());
        verify(bookingRepository, times(3)).save(any(Booking.class));
    }

    @Test
    void createBooking_InsufficientFunds() {
        // Arrange
        when(inventoryServiceAccessor.reserveSeats(flightId, 2)).thenReturn(true);
        when(paymentServiceAccessor.processPayment(any())).thenReturn(createInsufficientFundsResponse());
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setId(UUID.randomUUID());
            return booking;
        });

        // Act
        BookingResponse response = bookingService.createBooking(validRequest);

        // Assert
        assertEquals(BookingStatus.FAILED, response.getStatus());
        assertTrue(response.getFailureReason().contains("INSUFFICIENT_FUNDS"));
        verify(inventoryServiceAccessor).reserveSeats(flightId, 2);
        verify(inventoryServiceAccessor).releaseSeats(flightId, 2);
        verify(paymentServiceAccessor).processPayment(any());
    }

    @Test
    void createBooking_PaymentServiceFailure() {
        // Arrange
        when(inventoryServiceAccessor.reserveSeats(flightId, 2)).thenReturn(true);
        when(paymentServiceAccessor.processPayment(any())).thenThrow(new RuntimeException("Payment service unavailable"));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setId(UUID.randomUUID());
            return booking;
        });

        // Act
        BookingResponse response = bookingService.createBooking(validRequest);

        // Assert
        assertEquals(BookingStatus.FAILED, response.getStatus());
        assertEquals("Payment processing error", response.getFailureReason());
        verify(inventoryServiceAccessor).reserveSeats(flightId, 2);
        verify(inventoryServiceAccessor).releaseSeats(flightId, 2);
    }

    @Test
    void createBooking_ConcurrencyTest() throws InterruptedException {
        // Arrange
        ExecutorService executor = Executors.newFixedThreadPool(5);
        CountDownLatch latch = new CountDownLatch(5);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        when(inventoryServiceAccessor.reserveSeats(any(UUID.class), anyInt()))
            .thenReturn(true)
            .thenReturn(true)
            .thenReturn(false)
            .thenReturn(false)
            .thenReturn(false);

        when(paymentServiceAccessor.processPayment(any())).thenReturn(createSuccessfulPaymentResponse());
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setId(UUID.randomUUID());
            return booking;
        });

        // Act
        for (int i = 0; i < 5; i++) {
            executor.submit(() -> {
                try {
                    BookingResponse response = bookingService.createBooking(validRequest);
                    if (response.getStatus() == BookingStatus.CONFIRMED) {
                        successCount.incrementAndGet();
                    } else {
                        failureCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // Assert
        assertEquals(2, successCount.get());
        assertEquals(3, failureCount.get());
        verify(inventoryServiceAccessor, times(5)).reserveSeats(any(UUID.class), anyInt());
    }

    private PaymentResponse createSuccessfulPaymentResponse() {
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(UUID.randomUUID());
        response.setTransactionId(UUID.randomUUID().toString());
        response.setStatus(PaymentResponse.PaymentStatus.SUCCESS);
        response.setAmount(BigDecimal.valueOf(299.99));
        response.setCurrency("USD");
        response.setProcessedAt(LocalDateTime.now());
        return response;
    }

    private PaymentResponse createInsufficientFundsResponse() {
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(UUID.randomUUID());
        response.setTransactionId(UUID.randomUUID().toString());
        response.setStatus(PaymentResponse.PaymentStatus.FAILED);
        response.setAmount(BigDecimal.ZERO);
        response.setCurrency("USD");
        response.setFailureReason("INSUFFICIENT_FUNDS");
        response.setProcessedAt(LocalDateTime.now());
        return response;
    }
}