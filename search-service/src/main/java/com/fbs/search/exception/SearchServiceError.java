package com.fbs.search.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum SearchServiceError {

    // Validation Errors
    INVALID_SEARCH_CRITERIA(12001, "Invalid search criteria. Must be CHEAPEST or FASTEST", HttpStatus.BAD_REQUEST),
    INVALID_DATE_FORMAT(12002, "Invalid date format. Expected YYYY-MM-DD", HttpStatus.BAD_REQUEST),
    MISSING_REQUIRED_PARAMETER(12003, "Missing required parameter", HttpStatus.BAD_REQUEST),

    // Service Errors
    CACHE_SERVICE_ERROR(12011, "Cache service error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVENTORY_SERVICE_ERROR(12012, "Inventory service unavailable", HttpStatus.SERVICE_UNAVAILABLE),
    GRAPH_NOT_INITIALIZED(12013, "Flight graph not initialized", HttpStatus.SERVICE_UNAVAILABLE),

    // Internal Errors
    INTERNAL_SERVER_ERROR(12020, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);

    private final Integer errorCode;
    private final String message;
    private final HttpStatus status;
}
