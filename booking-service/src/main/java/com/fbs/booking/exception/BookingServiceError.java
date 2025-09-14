package com.fbs.booking.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum BookingServiceError {

    // Validation Errors
    INVALID_BOOKING_DATA(14001, "Invalid booking data", HttpStatus.BAD_REQUEST),
    MISSING_REQUIRED_PARAMETER(14002, "Missing required parameter", HttpStatus.BAD_REQUEST),
    INVALID_USER_ID(14003, "Invalid user ID", HttpStatus.BAD_REQUEST),
    INVALID_FLIGHT_ID(14004, "Invalid flight ID", HttpStatus.BAD_REQUEST),

    // Business Logic Errors
    BOOKING_NOT_FOUND(14011, "Booking not found", HttpStatus.NOT_FOUND),
    FLIGHT_NOT_AVAILABLE(14012, "Flight not available for booking", HttpStatus.CONFLICT),
    INSUFFICIENT_SEATS(14013, "Insufficient seats available", HttpStatus.CONFLICT),
    BOOKING_ALREADY_EXISTS(14014, "Booking already exists", HttpStatus.CONFLICT),
    BOOKING_ALREADY_CANCELLED(14015, "Booking already cancelled", HttpStatus.BAD_REQUEST),
    CANNOT_CANCEL_BOOKING(14016, "Cannot cancel booking", HttpStatus.BAD_REQUEST),

    // External Service Errors
    INVENTORY_SERVICE_ERROR(14021, "Inventory service unavailable", HttpStatus.BAD_GATEWAY),
    PAYMENT_SERVICE_ERROR(14022, "Payment service unavailable", HttpStatus.BAD_GATEWAY),

    // Database Errors
    DATABASE_ERROR(14031, "Database operation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    DATA_INTEGRITY_ERROR(14032, "Data integrity violation", HttpStatus.CONFLICT),

    // Internal Errors
    INTERNAL_SERVER_ERROR(14040, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);

    private final Integer errorCode;
    private final String message;
    private final HttpStatus status;
}