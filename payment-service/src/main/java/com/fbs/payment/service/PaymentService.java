package com.fbs.payment.service;

import com.fbs.payment.config.PaymentConfig;
import com.fbs.payment.dto.PaymentRequest;
import com.fbs.payment.dto.PaymentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Payment service simulator - configurable black box
 */
@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    private final Random random = new Random();

    @Autowired
    private PaymentConfig config;

    /**
     * Process payment - main method called by booking service
     */
    public PaymentResponse processPayment(PaymentRequest request) {
        logger.info("Processing payment for booking: {} amount: {}",
                   request.getBookingId(), request.getAmount());

        // Simulate processing delay
        simulateDelay();

        // Determine payment outcome
        PaymentResponse.PaymentStatus status = determinePaymentOutcome(request);
        String failureReason = getFailureReason(status);
        String transactionId = generateTransactionId();

        PaymentResponse response = PaymentResponse.builder()
                .paymentId(UUID.randomUUID())
                .bookingId(request.getBookingId())
                .status(status)
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .failureReason(failureReason)
                .transactionId(transactionId)
                .processedAt(LocalDateTime.now())
                .build();

        logger.info("Payment processed: booking={}, status={}, transactionId={}",
                   request.getBookingId(), status, transactionId);

        return response;
    }

    /**
     * Determine payment outcome based on configuration
     */
    private PaymentResponse.PaymentStatus determinePaymentOutcome(PaymentRequest request) {
        // Demo mode - predictable outcomes
        if (config.isDemoMode()) {
            return getDemoOutcome(request.getCustomerEmail());
        }

        // Random failures enabled
        if (config.isEnableRandomFailures()) {
            int randomValue = random.nextInt(100) + 1;

            if (randomValue <= config.getSuccessRate()) {
                return PaymentResponse.PaymentStatus.SUCCESS;
            } else {
                return PaymentResponse.PaymentStatus.FAILED;
            }
        }

        // Default success
        return PaymentResponse.PaymentStatus.SUCCESS;
    }

    /**
     * Demo mode outcomes based on email
     */
    private PaymentResponse.PaymentStatus getDemoOutcome(String email) {
        if (config.getDemoSuccessEmails() != null &&
            config.getDemoSuccessEmails().contains(email)) {
            return PaymentResponse.PaymentStatus.SUCCESS;
        }

        if (config.getDemoFailureEmails() != null &&
            config.getDemoFailureEmails().contains(email)) {
            return PaymentResponse.PaymentStatus.FAILED;
        }

        // Default random for other emails
        return random.nextInt(100) < config.getSuccessRate()
            ? PaymentResponse.PaymentStatus.SUCCESS
            : PaymentResponse.PaymentStatus.FAILED;
    }

    /**
     * Get failure reason based on configured rates
     */
    private String getFailureReason(PaymentResponse.PaymentStatus status) {
        if (status == PaymentResponse.PaymentStatus.SUCCESS) {
            return null;
        }

        int randomFailure = random.nextInt(100) + 1;
        int insufficientFunds = config.getFailures().getInsufficientFundsRate();
        int cardDeclined = config.getFailures().getCardDeclinedRate();
        int networkError = config.getFailures().getNetworkErrorRate();

        if (randomFailure <= insufficientFunds) {
            return "Insufficient funds";
        } else if (randomFailure <= insufficientFunds + cardDeclined) {
            return "Card declined by bank";
        } else if (randomFailure <= insufficientFunds + cardDeclined + networkError) {
            return "Network error - please retry";
        } else {
            return "Payment processing failed";
        }
    }

    /**
     * Simulate processing delay
     */
    private void simulateDelay() {
        try {
            int delay = config.getDelay().getMin() +
                       random.nextInt(config.getDelay().getMax() - config.getDelay().getMin() + 1);
            Thread.sleep(delay * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Payment processing delay interrupted", e);
        }
    }

    /**
     * Generate mock transaction ID
     */
    private String generateTransactionId() {
        return "TXN_" + System.currentTimeMillis() + "_" + random.nextInt(10000);
    }
}