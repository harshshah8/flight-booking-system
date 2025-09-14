package com.fbs.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Payment response to booking service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private UUID paymentId;
    private UUID bookingId;
    private PaymentStatus status;
    private BigDecimal amount;
    private String currency;
    private String failureReason;
    private String transactionId;
    private LocalDateTime processedAt;

    public enum PaymentStatus {
        SUCCESS,
        FAILED,
        PENDING,
        TIMEOUT
    }
}