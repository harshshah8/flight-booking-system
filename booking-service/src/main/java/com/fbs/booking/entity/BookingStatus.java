package com.fbs.booking.entity;

public enum BookingStatus {
    INITIATED("Booking initiated"),
    SEATS_RESERVED("Seats reserved"),
    CONFIRMED("Booking confirmed"),
    FAILED("Booking failed"),
    EXPIRED("Booking expired");

    private final String description;

    BookingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}