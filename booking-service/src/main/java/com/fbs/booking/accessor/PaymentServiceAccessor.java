package com.fbs.booking.accessor;

import com.fbs.booking.dto.PaymentRequest;
import com.fbs.booking.dto.PaymentResponse;
import com.fbs.booking.exception.BookingServiceError;
import com.fbs.booking.exception.BookingServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.time.Duration;

@Component
public class PaymentServiceAccessor {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceAccessor.class);
    private final WebClient webClient;

    public PaymentServiceAccessor(@Value("${external.services.payment-service.url}") String paymentServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(paymentServiceUrl)
                .build();
    }

    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        try {
            return webClient.post()
                    .uri("/v1/payments/process")
                    .bodyValue(paymentRequest)
                    .retrieve()
                    .bodyToMono(PaymentResponse.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();
        } catch (WebClientException e) {
            logger.error("Error processing payment for booking: {}", paymentRequest.getBookingId(), e);
            throw new BookingServiceException(BookingServiceError.PAYMENT_SERVICE_ERROR);
        }
    }
}