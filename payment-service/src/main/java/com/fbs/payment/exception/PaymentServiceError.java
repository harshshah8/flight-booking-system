package com.fbs.payment.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum PaymentServiceError {

    // Validation Errors
    INVALID_PAYMENT_DATA(15001, "Invalid payment data", HttpStatus.BAD_REQUEST),
    MISSING_REQUIRED_PARAMETER(15002, "Missing required parameter", HttpStatus.BAD_REQUEST),
    INVALID_AMOUNT(15003, "Invalid payment amount", HttpStatus.BAD_REQUEST),
    INVALID_PAYMENT_METHOD(15004, "Invalid payment method", HttpStatus.BAD_REQUEST),
    INVALID_BOOKING_ID(15005, "Invalid booking ID", HttpStatus.BAD_REQUEST),

    // Payment Processing Errors
    PAYMENT_NOT_FOUND(15011, "Payment not found", HttpStatus.NOT_FOUND),
    PAYMENT_ALREADY_PROCESSED(15012, "Payment already processed", HttpStatus.CONFLICT),
    PAYMENT_FAILED(15013, "Payment processing failed", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_FUNDS(15014, "Insufficient funds", HttpStatus.BAD_REQUEST),
    PAYMENT_DECLINED(15015, "Payment declined", HttpStatus.BAD_REQUEST),
    PAYMENT_EXPIRED(15016, "Payment session expired", HttpStatus.BAD_REQUEST),

    // External Gateway Errors
    PAYMENT_GATEWAY_ERROR(15021, "Payment gateway error", HttpStatus.BAD_GATEWAY),
    PAYMENT_GATEWAY_TIMEOUT(15022, "Payment gateway timeout", HttpStatus.GATEWAY_TIMEOUT),
    PAYMENT_GATEWAY_UNAVAILABLE(15023, "Payment gateway unavailable", HttpStatus.SERVICE_UNAVAILABLE),

    // Refund Errors
    REFUND_NOT_ALLOWED(15031, "Refund not allowed", HttpStatus.BAD_REQUEST),
    REFUND_ALREADY_PROCESSED(15032, "Refund already processed", HttpStatus.CONFLICT),
    REFUND_AMOUNT_EXCEEDS_PAYMENT(15033, "Refund amount exceeds original payment", HttpStatus.BAD_REQUEST),

    // Database Errors
    DATABASE_ERROR(15041, "Database operation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    DATA_INTEGRITY_ERROR(15042, "Data integrity violation", HttpStatus.CONFLICT),

    // Internal Errors
    INTERNAL_SERVER_ERROR(15050, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);

    private final Integer errorCode;
    private final String message;
    private final HttpStatus status;
}