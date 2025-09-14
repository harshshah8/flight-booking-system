package com.fbs.booking.accessor;

import com.fbs.booking.dto.FlightDto;
import com.fbs.booking.exception.BookingServiceError;
import com.fbs.booking.exception.BookingServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.time.Duration;
import java.util.UUID;

@Component
public class InventoryServiceAccessor {

    private static final Logger logger = LoggerFactory.getLogger(InventoryServiceAccessor.class);
    private final WebClient webClient;

    public InventoryServiceAccessor(@Value("${external.services.inventory-service.url}") String inventoryServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(inventoryServiceUrl)
                .build();
    }

    public FlightDto getFlightById(UUID flightId) {
        try {
            return webClient.get()
                    .uri("/v1/flights/{flightId}", flightId)
                    .retrieve()
                    .bodyToMono(FlightDto.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();
        } catch (WebClientException e) {
            logger.error("Error fetching flight by ID: {}", flightId, e);
            throw new BookingServiceException(BookingServiceError.INVENTORY_SERVICE_ERROR);
        }
    }

    public boolean reserveSeats(UUID flightId, Integer numberOfSeats) {
        try {
            Boolean result = webClient.post()
                    .uri("/v1/flights/{flightId}/reserve-seats?numberOfSeats={numberOfSeats}", flightId, numberOfSeats)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();
            return result != null && result;
        } catch (WebClientException e) {
            logger.error("Error reserving seats for flight: {}", flightId, e);
            throw new BookingServiceException(BookingServiceError.INVENTORY_SERVICE_ERROR);
        }
    }

    public void releaseSeats(UUID flightId, Integer numberOfSeats) {
        try {
            webClient.post()
                    .uri("/v1/flights/{flightId}/release-seats?numberOfSeats={numberOfSeats}", flightId, numberOfSeats)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();
        } catch (WebClientException e) {
            logger.error("Error releasing seats for flight: {}", flightId, e);
            throw new BookingServiceException(BookingServiceError.INVENTORY_SERVICE_ERROR);
        }
    }
}