package com.fbs.search.model;

import java.math.BigDecimal;
import java.util.UUID;

public class FlightEdge {
    private UUID flightId;
    private String source;
    private String destination;
    private BigDecimal cost;
    private Integer duration;
    private String flightNumber;

    public FlightEdge(UUID flightId, String source, String destination, 
                     BigDecimal cost, Integer duration, String flightNumber) {
        this.flightId = flightId;
        this.source = source;
        this.destination = destination;
        this.cost = cost;
        this.duration = duration;
        this.flightNumber = flightNumber;
    }

    public UUID getFlightId() {
        return flightId;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public Integer getDuration() {
        return duration;
    }

    public String getFlightNumber() {
        return flightNumber;
    }
}