package com.fbs.inventory.controller;

import com.fbs.inventory.entity.Flight;
import com.fbs.inventory.service.FlightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private FlightService flightService;

    @PostMapping("/flights")
    public ResponseEntity<Flight> addFlight(@RequestBody Flight flight) {
        logger.info("Admin adding new flight: {}", flight.getFlightNumber());
        Flight savedFlight = flightService.addFlight(flight);
        return ResponseEntity.ok(savedFlight);
    }

    @PutMapping("/flights/{flightId}/cancel")
    public ResponseEntity<Flight> cancelFlight(@PathVariable UUID flightId) {
        logger.info("Admin cancelling flight: {}", flightId);
        Flight cancelledFlight = flightService.cancelFlight(flightId);
        if (cancelledFlight != null) {
            return ResponseEntity.ok(cancelledFlight);
        }
        return ResponseEntity.notFound().build();
    }
}