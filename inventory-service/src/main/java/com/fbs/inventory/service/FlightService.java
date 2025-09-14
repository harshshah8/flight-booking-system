package com.fbs.inventory.service;

import com.fbs.inventory.entity.Flight;
import com.fbs.inventory.repository.FlightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class FlightService {

    private static final Logger logger = LoggerFactory.getLogger(FlightService.class);

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public List<Flight> getAllFlights() {
        return flightRepository.findAllActiveFlights();
    }

    public Flight getFlightById(UUID flightId) {
        return flightRepository.findByFlightId(flightId);
    }

    public List<Flight> getFlightsByRoute(String source, String destination) {
        return flightRepository.findBySourceAndDestination(source, destination);
    }

    public List<Flight> getFlightsBySource(String source) {
        return flightRepository.findBySource(source);
    }

    /**
     * Atomically reserve seats for a flight
     * @return true if seats were reserved, false if insufficient seats
     */
    @Transactional
    public boolean reserveSeats(UUID flightId, Integer numberOfSeats) {
        int rowsUpdated = flightRepository.reserveSeats(flightId, numberOfSeats);
        return rowsUpdated > 0;
    }

    /**
     * Release seats for a flight (rollback operation)
     */
    @Transactional
    public void releaseSeats(UUID flightId, Integer numberOfSeats) {
        flightRepository.releaseSeats(flightId, numberOfSeats);
    }

    // Admin operations
    @Transactional
    public Flight addFlight(Flight flight) {
        return flightRepository.save(flight);
    }

    @Transactional
    public Flight cancelFlight(UUID flightId) {
        Flight flight = flightRepository.findByFlightId(flightId);
        if (flight != null) {
            flight.setFlightStatus(com.fbs.inventory.entity.FlightStatus.CANCELLED);
            Flight cancelledFlight = flightRepository.save(flight);

            // Publish event for cancelled flight
            publishFlightCancelledEvent(cancelledFlight);

            return cancelledFlight;
        }
        return null;
    }

    private void publishFlightCancelledEvent(Flight flight) {
        try {
            String eventMessage = String.format("%s:%s:%s",
                "FLIGHT_CANCELLED", flight.getSource(), flight.getDestination());
            redisTemplate.convertAndSend("flight-events", eventMessage);
            logger.info("Published flight cancelled event for {}→{}", flight.getSource(), flight.getDestination());
        } catch (Exception e) {
            logger.error("Failed to publish flight cancelled event: {}", e.getMessage());
        }
    }
}