package com.fbs.booking.repository;

import com.fbs.booking.entity.Booking;
import com.fbs.booking.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    /**
     * Find expired bookings that are still in SEATS_RESERVED status
     */
    @Query("SELECT b FROM Booking b WHERE b.bookingStatus = :status AND b.createdAt < :expiredBefore")
    List<Booking> findExpiredReservations(
        @Param("status") BookingStatus status,
        @Param("expiredBefore") LocalDateTime expiredBefore
    );

    /**
     * Find bookings by customer email
     */
    List<Booking> findByCustomerEmailOrderByCreatedAtDesc(String customerEmail);

    /**
     * Find bookings by flight ID
     */
    List<Booking> findByFlightId(UUID flightId);

    /**
     * Count confirmed bookings for a flight
     */
    @Query("SELECT COALESCE(SUM(b.numberOfSeats), 0) FROM Booking b WHERE b.flightId = :flightId AND b.bookingStatus IN (:statuses)")
    Integer countBookedSeatsByFlightAndStatus(
        @Param("flightId") UUID flightId,
        @Param("statuses") List<BookingStatus> statuses
    );
}