package com.fbs.search.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class FlightEventListener {

    private static final Logger logger = LoggerFactory.getLogger(FlightEventListener.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private GraphService graphService;

    public void handleFlightEvent(String message) {
        try {
            logger.info("Received flight event: {}", message);

            String[] parts = message.split(":");
            if (parts.length >= 3) {
                String eventType = parts[0];
                String source = parts[1];
                String destination = parts[2];

                if ("FLIGHT_CANCELLED".equals(eventType)) {
                    logger.info("Processing flight cancellation for {}→{}", source, destination);

                    // Flush cache keys for this route
                    flushCacheForRoute(source, destination);

                    // Reinitialize graphs
                    graphService.initializeGraphs();
                    logger.info("Graphs reinitialized after flight cancellation");
                }
            }

        } catch (Exception e) {
            logger.error("Error processing flight event: {}", e.getMessage(), e);
        }
    }

    private void flushCacheForRoute(String source, String destination) {
        try {
            // Find and delete all cache keys containing this source-destination pair
            String pattern1 = source + ":" + destination + ":*";
            String pattern2 = destination + ":" + source + ":*";

            Set<String> keysToDelete1 = redisTemplate.keys(pattern1);
            Set<String> keysToDelete2 = redisTemplate.keys(pattern2);

            int deletedCount = 0;
            if (keysToDelete1 != null && !keysToDelete1.isEmpty()) {
                redisTemplate.delete(keysToDelete1);
                deletedCount += keysToDelete1.size();
            }

            if (keysToDelete2 != null && !keysToDelete2.isEmpty()) {
                redisTemplate.delete(keysToDelete2);
                deletedCount += keysToDelete2.size();
            }

            logger.info("Flushed {} cache keys for route {}↔{}", deletedCount, source, destination);

        } catch (Exception e) {
            logger.error("Error flushing cache for route {}↔{}: {}", source, destination, e.getMessage());
        }
    }
}