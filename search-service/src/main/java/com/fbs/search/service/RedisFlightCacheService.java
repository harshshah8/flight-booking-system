package com.fbs.search.service;

import com.fbs.search.exception.SearchServiceError;
import com.fbs.search.exception.SearchServiceException;
import com.fbs.search.model.CachedFlightPath;
import com.fbs.search.model.CachedSearchResult;
import com.fbs.search.model.FlightPath;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class RedisFlightCacheService {

    private static final Logger logger = LoggerFactory.getLogger(RedisFlightCacheService.class);
    private static final int CACHE_TTL_DAYS = 180; // 6 months

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void cacheSearchResults(String source, String destination, LocalDate date,
                                 String criteria, List<FlightPath> paths) {
        try {
            String key = buildKey(source, destination, date, criteria);

            // Check if key already exists - skip if it does
            if (redisTemplate.hasKey(key)) {
                logger.debug("Key already exists, skipping: {}", key);
                return;
            }

            // Convert FlightPath to CachedFlightPath
            List<CachedFlightPath> cachedPaths = paths.stream()
                    .map(this::convertToCachedPath)
                    .collect(Collectors.toList());

            CachedSearchResult result = new CachedSearchResult(cachedPaths);
            String jsonValue = objectMapper.writeValueAsString(result);

            redisTemplate.opsForValue().set(key, jsonValue, CACHE_TTL_DAYS, TimeUnit.DAYS);

            logger.debug("Cached {} paths for key: {}", paths.size(), key);

        } catch (JsonProcessingException e) {
            logger.error("Error serializing search results for cache: {}", e.getMessage());
            throw new SearchServiceException(SearchServiceError.CACHE_SERVICE_ERROR);
        } catch (Exception e) {
            logger.error("Error caching search results", e);
            throw new SearchServiceException(SearchServiceError.CACHE_SERVICE_ERROR);
        }
    }

    public Optional<CachedSearchResult> getCachedResults(String source, String destination, 
                                                       LocalDate date, String criteria) {
        try {
            String key = buildKey(source, destination, date, criteria);
            String jsonValue = redisTemplate.opsForValue().get(key);
            
            if (jsonValue != null) {
                CachedSearchResult result = objectMapper.readValue(jsonValue, CachedSearchResult.class);
                logger.debug("Cache hit for key: {}", key);
                return Optional.of(result);
            }
            
            logger.debug("Cache miss for key: {}", key);
            return Optional.empty();
            
        } catch (JsonProcessingException e) {
            logger.error("Error deserializing cached search results", e);
            return Optional.empty();
        }
    }

    public void preComputeAndCacheAll(String source, String destination, List<FlightPath> cheapestPaths, 
                                    List<FlightPath> fastestPaths) {
        LocalDate startDate = LocalDate.now();
        int skippedKeys = 0;
        int newKeys = 0;
        
        // Cache for next 6 months only if paths exist
        for (int days = 0; days < CACHE_TTL_DAYS; days++) {
            LocalDate date = startDate.plusDays(days);
            
            // Only cache if paths exist
            if (!cheapestPaths.isEmpty()) {
                String cheapestKey = buildKey(source, destination, date, "CHEAPEST");
                if (redisTemplate.hasKey(cheapestKey)) {
                    skippedKeys++;
                } else {
                    cacheSearchResults(source, destination, date, "CHEAPEST", cheapestPaths);
                    newKeys++;
                }
            }
            if (!fastestPaths.isEmpty()) {
                String fastestKey = buildKey(source, destination, date, "FASTEST");
                if (redisTemplate.hasKey(fastestKey)) {
                    skippedKeys++;
                } else {
                    cacheSearchResults(source, destination, date, "FASTEST", fastestPaths);
                    newKeys++;
                }
            }
        }
        
        if (skippedKeys > 0) {
            logger.debug("Pre-computed cache for {}:{} - New: {}, Skipped: {} (already existed)", 
                        source, destination, newKeys, skippedKeys);
        }
    }

    private String buildKey(String source, String destination, LocalDate date, String criteria) {
        return String.format("%s:%s:%s:%s", source, destination, date.toString(), criteria);
    }

    private CachedFlightPath convertToCachedPath(FlightPath flightPath) {
        List<UUID> flightIds = flightPath.getFlights().stream()
                .map(flight -> flight.getFlightId())
                .collect(Collectors.toList());
        
        return new CachedFlightPath(
            flightPath.getTotalCost(),
            flightPath.getTotalDuration(),
            flightIds
        );
    }

    public void clearCache(String source, String destination) {
        String pattern = source + ":" + destination + ":*";
        redisTemplate.delete(redisTemplate.keys(pattern));
        logger.info("Cleared cache for route: {}:{}", source, destination);
    }

    public long getCacheSize() {
        return redisTemplate.getConnectionFactory().getConnection().dbSize();
    }
}