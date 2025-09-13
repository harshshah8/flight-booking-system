package com.fbs.search.service;

import com.fbs.search.accessor.InventoryServiceAccessor;
import com.fbs.search.dto.Flight;
import com.fbs.search.exception.SearchServiceError;
import com.fbs.search.exception.SearchServiceException;
import com.fbs.search.model.FlightEdge;
import com.fbs.search.model.FlightGraph;
import com.fbs.search.model.FlightPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Service
public class GraphService {

    private static final Logger logger = LoggerFactory.getLogger(GraphService.class);

    @Autowired
    private InventoryServiceAccessor inventoryServiceAccessor;

    @Autowired
    private FlightSearchAlgorithm searchAlgorithm;

    @Autowired
    private RedisFlightCacheService cacheService;

    private FlightGraph costGraph;
    private FlightGraph durationGraph;

    @PostConstruct
    public void initializeGraphs() {
        try {
            logger.info("Pre-computing flight graphs...");
            List<Flight> allFlights = inventoryServiceAccessor.getAllFlights();

            if (allFlights == null || allFlights.isEmpty()) {
                logger.error("No flights received from inventory service");
                throw new SearchServiceException(SearchServiceError.INVENTORY_SERVICE_ERROR);
            }

            costGraph = new FlightGraph();
            durationGraph = new FlightGraph();

            for (Flight flight : allFlights) {
                try {
                    FlightEdge edge = new FlightEdge(
                        flight.getFlightId(),
                        flight.getSource(),
                        flight.getDestination(),
                        flight.getCost(),
                        flight.getDuration(),
                        flight.getFlightNumber()
                    );

                    costGraph.addEdge(edge);
                    durationGraph.addEdge(edge);
                } catch (Exception e) {
                    logger.warn("Failed to process flight: {}", flight.getFlightId(), e);
                }
            }

            logger.info("Graphs pre-computed successfully! Cities: {}, Flights: {}",
                       costGraph.getCities().size(), allFlights.size());

            // Pre-compute and cache K-shortest paths for all city pairs
            preComputeAllPaths();

        } catch (SearchServiceException e) {
            logger.error("Failed to initialize flight graphs: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during graph initialization", e);
            throw new SearchServiceException(SearchServiceError.GRAPH_NOT_INITIALIZED);
        }
    }

    private void preComputeAllPaths() {
        try {
            logger.info("Starting pre-computation of all K-shortest paths...");
            int totalPairs = 0;
            int cachedPairs = 0;

            for (String source : costGraph.getCities()) {
                for (String destination : costGraph.getCities()) {
                    if (!source.equals(destination)) {
                        try {
                            // Find top 10 cheapest and fastest paths
                            List<FlightPath> cheapestPaths = searchAlgorithm.findCheapestPaths(costGraph, source, destination, 10);
                            List<FlightPath> fastestPaths = searchAlgorithm.findFastestPaths(durationGraph, source, destination, 10);

                            // Only cache if paths exist
                            if (!cheapestPaths.isEmpty() || !fastestPaths.isEmpty()) {
                                cacheService.preComputeAndCacheAll(source, destination, cheapestPaths, fastestPaths);
                                cachedPairs++;
                            }
                        } catch (Exception e) {
                            logger.warn("Failed to compute paths for route {}:{}", source, destination, e);
                        }
                        totalPairs++;
                    }
                }
            }

            logger.info("Pre-computed {} city pairs. Cached {} pairs with connections. Cache entries: {}",
                       totalPairs, cachedPairs, cacheService.getCacheSize());

        } catch (Exception e) {
            logger.error("Error during pre-computation of paths", e);
            // Don't throw exception here as partial pre-computation is acceptable
        }
    }

    public FlightGraph getCostGraph() {
        return costGraph;
    }

    public FlightGraph getDurationGraph() {
        return durationGraph;
    }
}