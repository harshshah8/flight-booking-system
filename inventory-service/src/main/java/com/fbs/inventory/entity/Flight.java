package com.fbs.inventory.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "flights")
public class Flight {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "flight_id")
    private UUID flightId;
    
    @Column(name = "flight_number", nullable = false)
    private String flightNumber;
    
    @Column(name = "source", nullable = false, length = 10)
    private String source;
    
    @Column(name = "destination", nullable = false, length = 10)
    private String destination;
    
    @Column(name = "cost", nullable = false)
    private Double cost;
    
    @Column(name = "duration", nullable = false)
    private Integer duration; // in minutes
    
    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;
    
    @Column(name = "occupied_seats", nullable = false)
    private Integer occupiedSeats;

    @Column(name = "booked_seats", nullable = false)
    private Integer bookedSeats = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "flight_status", nullable = false)
    private FlightStatus flightStatus;
    
    @Column(name = "departure_time", nullable = false)
    private LocalTime departureTime;
    
    @Column(name = "arrival_time", nullable = false)
    private LocalTime arrivalTime;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public Flight() {}
    
    // Constructor
    public Flight(String flightNumber, String source, String destination, 
                  Double cost, Integer duration, Integer availableSeats, 
                  Integer occupiedSeats, FlightStatus flightStatus, 
                  LocalTime departureTime, LocalTime arrivalTime) {
        this.flightNumber = flightNumber;
        this.source = source;
        this.destination = destination;
        this.cost = cost;
        this.duration = duration;
        this.availableSeats = availableSeats;
        this.occupiedSeats = occupiedSeats;
        this.flightStatus = flightStatus;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
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
    
    public FlightStatus getFlightStatus() { return flightStatus; }
    public void setFlightStatus(FlightStatus flightStatus) { this.flightStatus = flightStatus; }
    
    public LocalTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalTime departureTime) { this.departureTime = departureTime; }
    
    public LocalTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalTime arrivalTime) { this.arrivalTime = arrivalTime; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}