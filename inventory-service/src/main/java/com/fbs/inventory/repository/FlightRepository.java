package com.fbs.inventory.repository;

import com.fbs.inventory.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface FlightRepository extends JpaRepository<Flight, UUID> {
    
    // Find flight by ID
    Flight findByFlightId(UUID flightId);
    
    // Find flights by source and destination
    List<Flight> findBySourceAndDestination(String source, String destination);
    
    // Find flights by source
    List<Flight> findBySource(String source);
    
    // Find available flights (with available seats > 0)
    @Query("SELECT f FROM Flight f WHERE f.availableSeats > 0")
    List<Flight> findAvailableFlights();
    
    // Find flights by route and date range with available seats
    @Query("SELECT f FROM Flight f WHERE f.source = :source AND f.destination = :destination " +
           "AND f.departureTime BETWEEN :startTime AND :endTime AND f.availableSeats > 0")
    List<Flight> findAvailableFlightsByRouteAndDateRange(
            @Param("source") String source,
            @Param("destination") String destination,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
    
    // Find cheapest flights
    @Query("SELECT f FROM Flight f WHERE f.source = :source AND f.destination = :destination " +
           "AND f.availableSeats > 0 ORDER BY f.cost ASC")
    List<Flight> findCheapestFlights(@Param("source") String source, @Param("destination") String destination);
    
    // Find fastest flights
    @Query("SELECT f FROM Flight f WHERE f.source = :source AND f.destination = :destination " +
           "AND f.availableSeats > 0 ORDER BY f.duration ASC")
    List<Flight> findFastestFlights(@Param("source") String source, @Param("destination") String destination);

    // Atomic seat reservation - returns number of rows updated
    @Query("UPDATE Flight f " +
            "SET f.bookedSeats = f.bookedSeats + :numberOfSeats, " +
            "    f.availableSeats = f.availableSeats - :numberOfSeats " +
            "WHERE f.flightId = :flightId " +
            "  AND f.availableSeats >= :numberOfSeats")
    @Modifying
    int reserveSeats(@Param("flightId") UUID flightId,
                     @Param("numberOfSeats") Integer numberOfSeats);

    // Release seats (for rollback operations)
    @Query("UPDATE Flight f " +
            "SET f.bookedSeats = f.bookedSeats - :numberOfSeats, " +
            "    f.availableSeats = f.availableSeats + :numberOfSeats " +
            "WHERE f.flightId = :flightId " +
            "  AND f.bookedSeats >= :numberOfSeats")
    @Modifying
    int releaseSeats(@Param("flightId") UUID flightId,
                     @Param("numberOfSeats") Integer numberOfSeats);

    // Find all flights excluding CANCELLED and SOLD_OUT
    @Query("SELECT f FROM Flight f WHERE f.flightStatus NOT IN ('CANCELLED', 'SOLD_OUT')")
    List<Flight> findAllActiveFlights();
}