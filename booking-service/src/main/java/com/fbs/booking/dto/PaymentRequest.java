package com.fbs.booking.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class PaymentRequest {

    private UUID bookingId;
    private BigDecimal amount;
    private String currency;
    private String customerEmail;

    public PaymentRequest() {}

    public PaymentRequest(UUID bookingId, BigDecimal amount, String currency, String customerEmail) {
        this.bookingId = bookingId;
        this.amount = amount;
        this.currency = currency;
        this.customerEmail = customerEmail;
    }

    // Getters and Setters
    public UUID getBookingId() { return bookingId; }
    public void setBookingId(UUID bookingId) { this.bookingId = bookingId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
}