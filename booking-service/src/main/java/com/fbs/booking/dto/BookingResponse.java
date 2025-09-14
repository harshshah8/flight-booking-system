package com.fbs.booking.dto;

import com.fbs.booking.entity.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class BookingResponse {

    private UUID bookingId;
    private UUID flightId;
    private String customerEmail;
    private Integer numberOfSeats;
    private BookingStatus status;
    private BigDecimal totalAmount;
    private UUID paymentId;
    private String failureReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BookingResponse() {}

    // Builder pattern for easy construction
    public static BookingResponseBuilder builder() {
        return new BookingResponseBuilder();
    }

    // Getters and Setters
    public UUID getBookingId() { return bookingId; }
    public void setBookingId(UUID bookingId) { this.bookingId = bookingId; }

    public UUID getFlightId() { return flightId; }
    public void setFlightId(UUID flightId) { this.flightId = flightId; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public Integer getNumberOfSeats() { return numberOfSeats; }
    public void setNumberOfSeats(Integer numberOfSeats) { this.numberOfSeats = numberOfSeats; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public UUID getPaymentId() { return paymentId; }
    public void setPaymentId(UUID paymentId) { this.paymentId = paymentId; }

    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Builder class
    public static class BookingResponseBuilder {
        private BookingResponse response = new BookingResponse();

        public BookingResponseBuilder bookingId(UUID bookingId) {
            response.setBookingId(bookingId);
            return this;
        }

        public BookingResponseBuilder flightId(UUID flightId) {
            response.setFlightId(flightId);
            return this;
        }

        public BookingResponseBuilder customerEmail(String customerEmail) {
            response.setCustomerEmail(customerEmail);
            return this;
        }

        public BookingResponseBuilder numberOfSeats(Integer numberOfSeats) {
            response.setNumberOfSeats(numberOfSeats);
            return this;
        }

        public BookingResponseBuilder status(BookingStatus status) {
            response.setStatus(status);
            return this;
        }

        public BookingResponseBuilder totalAmount(BigDecimal totalAmount) {
            response.setTotalAmount(totalAmount);
            return this;
        }

        public BookingResponseBuilder paymentId(UUID paymentId) {
            response.setPaymentId(paymentId);
            return this;
        }

        public BookingResponseBuilder failureReason(String failureReason) {
            response.setFailureReason(failureReason);
            return this;
        }

        public BookingResponseBuilder createdAt(LocalDateTime createdAt) {
            response.setCreatedAt(createdAt);
            return this;
        }

        public BookingResponseBuilder updatedAt(LocalDateTime updatedAt) {
            response.setUpdatedAt(updatedAt);
            return this;
        }

        public BookingResponse build() {
            return response;
        }
    }
}