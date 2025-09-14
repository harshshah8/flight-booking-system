package com.fbs.inventory.controller;

import com.fbs.inventory.entity.Flight;
import com.fbs.inventory.service.FlightService;
import jakarta.annotation.Nonnull;
import org.springframework.lang.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("v1/flights")
public class FlightController {

    @Autowired
    private FlightService flightService;

    @GetMapping("/all")
    public ResponseEntity<List<Flight>> getAllFlights() {
        List<Flight> flights = flightService.getAllFlights();
        return ResponseEntity.ok(flights);
    }

    @GetMapping("/{flightId}")
    public ResponseEntity<Flight> getFlightById(@PathVariable @Nonnull UUID flightId) {
        Flight flight = flightService.getFlightById(flightId);
        if (flight != null) {
            return ResponseEntity.ok(flight);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/route")
    public ResponseEntity<List<Flight>> getFlightsByRoute(
            @RequestParam String source,
            @RequestParam String destination) {
        List<Flight> flights = flightService.getFlightsByRoute(source, destination);
        return ResponseEntity.ok(flights);
    }

    @GetMapping("/source/{source}")
    public ResponseEntity<List<Flight>> getFlightsBySource(@PathVariable String source) {
        List<Flight> flights = flightService.getFlightsBySource(source);
        return ResponseEntity.ok(flights);
    }

    @PostMapping("/{flightId}/reserve-seats")
    public ResponseEntity<Boolean> reserveSeats(@PathVariable UUID flightId, @RequestParam Integer numberOfSeats) {
        boolean reserved = flightService.reserveSeats(flightId, numberOfSeats);
        return ResponseEntity.ok(reserved);
    }

    @PostMapping("/{flightId}/release-seats")
    public ResponseEntity<Void> releaseSeats(@PathVariable UUID flightId, @RequestParam Integer numberOfSeats) {
        flightService.releaseSeats(flightId, numberOfSeats);
        return ResponseEntity.ok().build();
    }
}