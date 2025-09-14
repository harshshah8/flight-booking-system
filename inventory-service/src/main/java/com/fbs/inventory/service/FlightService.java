package com.fbs.inventory.service;

import com.fbs.inventory.entity.Flight;
import com.fbs.inventory.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class FlightService {

    @Autowired
    private FlightRepository flightRepository;

    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
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
}