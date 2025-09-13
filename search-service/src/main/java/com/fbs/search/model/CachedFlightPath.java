package com.fbs.search.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class CachedFlightPath {
    private BigDecimal cost;
    private Integer duration;
    private List<UUID> flights;

    public CachedFlightPath() {}

    public CachedFlightPath(BigDecimal cost, Integer duration, List<UUID> flights) {
        this.cost = cost;
        this.duration = duration;
        this.flights = flights;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public List<UUID> getFlights() {
        return flights;
    }

    public void setFlights(List<UUID> flights) {
        this.flights = flights;
    }

    public int getStops() {
        return flights.size() - 1;
    }
}