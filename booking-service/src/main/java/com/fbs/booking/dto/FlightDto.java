package com.fbs.booking.dto;

import java.time.LocalTime;
import java.util.UUID;

public class FlightDto {

    private UUID flightId;
    private String flightNumber;
    private String source;
    private String destination;
    private Double cost;
    private Integer duration;
    private Integer availableSeats;
    private Integer occupiedSeats;
    private Integer bookedSeats;
    private LocalTime departureTime;
    private LocalTime arrivalTime;

    public FlightDto() {}

    // Getters and Setters
    public UUID getFlightId() { return flightId; }
    public void setFlightId(UUID flightId) { this.flightId = flightId; }

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public Double getCost() { return cost; }
    public void setCost(Double cost) { this.cost = cost; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public Integer getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(Integer availableSeats) { this.availableSeats = availableSeats; }

    public Integer getOccupiedSeats() { return occupiedSeats; }
    public void setOccupiedSeats(Integer occupiedSeats) { this.occupiedSeats = occupiedSeats; }

    public Integer getBookedSeats() { return bookedSeats; }
    public void setBookedSeats(Integer bookedSeats) { this.bookedSeats = bookedSeats; }

    public LocalTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalTime departureTime) { this.departureTime = departureTime; }

    public LocalTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalTime arrivalTime) { this.arrivalTime = arrivalTime; }

    // Helper method to get remaining seats
    public Integer getRemainingSeats() {
        return availableSeats - bookedSeats;
    }
}