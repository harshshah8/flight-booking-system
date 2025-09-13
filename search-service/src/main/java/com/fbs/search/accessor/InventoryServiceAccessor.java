package com.fbs.search.accessor;

import com.fbs.search.dto.Flight;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Component
public class InventoryServiceAccessor {

    private final WebClient webClient;

    public InventoryServiceAccessor(@Value("${inventory.service.url}") String inventoryServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(inventoryServiceUrl)
                .build();
    }

    public List<Flight> getAllFlights() {
        return webClient.get()
                .uri("/v1/flights/all")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Flight>>() {})
                .block();
    }

    public Flight getFlightById(UUID flightId) {
        return webClient.get()
                .uri("/v1/flights/{flightId}", flightId)
                .retrieve()
                .bodyToMono(Flight.class)
                .block();
    }
}