package com.fbs.search.accessor;

import com.fbs.search.dto.Flight;
import com.fbs.search.exception.SearchServiceError;
import com.fbs.search.exception.SearchServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Component
public class InventoryServiceAccessor {

    private static final Logger logger = LoggerFactory.getLogger(InventoryServiceAccessor.class);
    private final WebClient webClient;

    public InventoryServiceAccessor(@Value("${inventory.service.url}") String inventoryServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(inventoryServiceUrl)
                .build();
    }

    /**
     * Get all flights from inventory service
     * @throws SearchServiceException if inventory service is unavailable
     */
    public List<Flight> getAllFlights() {
        try {
            logger.debug("Fetching all flights from inventory service");
            List<Flight> flights = webClient.get()
                    .uri("/v1/flights/all")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Flight>>() {})
                    .timeout(Duration.ofSeconds(30))
                    .block();

            logger.info("Successfully fetched {} flights from inventory service",
                       flights != null ? flights.size() : 0);
            return flights;

        } catch (WebClientException e) {
            logger.error("Error communicating with inventory service", e);
            throw new SearchServiceException(SearchServiceError.INVENTORY_SERVICE_ERROR);
        } catch (Exception e) {
            logger.error("Unexpected error while fetching flights", e);
            throw new SearchServiceException(SearchServiceError.INTERNAL_SERVER_ERROR);
        }
    }

    public Flight getFlightById(UUID flightId) {
        try {
            return webClient.get()
                    .uri("/v1/flights/{flightId}", flightId)
                    .retrieve()
                    .bodyToMono(Flight.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();
        } catch (WebClientException e) {
            logger.error("Error fetching flight by ID: {}", flightId, e);
            throw new SearchServiceException(SearchServiceError.INVENTORY_SERVICE_ERROR);
        }
    }
}