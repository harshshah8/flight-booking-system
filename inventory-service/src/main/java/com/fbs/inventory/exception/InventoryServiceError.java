package com.fbs.inventory.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum InventoryServiceError {

    // Validation Errors
    INVALID_FLIGHT_ID(13001, "Invalid flight ID", HttpStatus.BAD_REQUEST),
    MISSING_REQUIRED_PARAMETER(13002, "Missing required parameter", HttpStatus.BAD_REQUEST),
    INVALID_FLIGHT_DATA(13003, "Invalid flight data", HttpStatus.BAD_REQUEST),

    // Database Errors
    FLIGHT_NOT_FOUND(13011, "Flight not found", HttpStatus.NOT_FOUND),
    DATABASE_ERROR(13012, "Database operation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    DATA_INTEGRITY_ERROR(13013, "Data integrity violation", HttpStatus.CONFLICT),

    // Business Logic Errors
    FLIGHT_ALREADY_EXISTS(13021, "Flight already exists", HttpStatus.CONFLICT),
    INVALID_FLIGHT_STATUS(13022, "Invalid flight status", HttpStatus.BAD_REQUEST),

    // Internal Errors
    INTERNAL_SERVER_ERROR(13030, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);

    private final Integer errorCode;
    private final String message;
    private final HttpStatus status;
}