package com.fbs.search.model;

import java.math.BigDecimal;
import java.util.List;

public class FlightPath {
    private List<FlightEdge> flights;
    private BigDecimal totalCost;
    private Integer totalDuration;

    public FlightPath(List<FlightEdge> flights) {
        this.flights = flights;
        this.totalCost = flights.stream()
                .map(FlightEdge::getCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.totalDuration = flights.stream()
                .mapToInt(FlightEdge::getDuration)
                .sum();
    }

    public List<FlightEdge> getFlights() {
        return flights;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public Integer getTotalDuration() {
        return totalDuration;
    }

    public int getStops() {
        return flights.size() - 1;
    }
}