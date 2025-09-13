package com.fbs.search.controller;

import com.fbs.search.model.CachedSearchResult;
import com.fbs.search.service.RedisFlightCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/v1/search")
public class SearchController {

    @Autowired
    private RedisFlightCacheService cacheService;

    @GetMapping
    public ResponseEntity<CachedSearchResult> searchFlights(
            @RequestParam String source,
            @RequestParam String destination, 
            @RequestParam String date,
            @RequestParam String criteria) {
        
        // Validate criteria
        if (!criteria.equalsIgnoreCase("CHEAPEST") && !criteria.equalsIgnoreCase("FASTEST")) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            LocalDate searchDate = LocalDate.parse(date);
            
            Optional<CachedSearchResult> result = cacheService.getCachedResults(
                source.toUpperCase(), 
                destination.toUpperCase(), 
                searchDate, 
                criteria.toUpperCase()
            );

            return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}