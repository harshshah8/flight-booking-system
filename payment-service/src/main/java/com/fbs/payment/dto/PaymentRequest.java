package com.fbs.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Payment request from booking service
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    private UUID bookingId;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod; // CREDIT_CARD, DEBIT_CARD, UPI
    private String customerEmail;
    private String customerName;

    // Payment method details (simplified)
    private String cardNumber;
    private String cardExpiry;
    private String cardCvv;
}