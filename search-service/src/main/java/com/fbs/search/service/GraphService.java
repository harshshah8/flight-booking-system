package com.fbs.search.service;

import com.fbs.search.accessor.InventoryServiceAccessor;
import com.fbs.search.dto.Flight;
import com.fbs.search.model.FlightEdge;
import com.fbs.search.model.FlightGraph;
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

    private FlightGraph costGraph;
    private FlightGraph durationGraph;

    @PostConstruct
    public void initializeGraphs() {
        logger.info("Pre-computing flight graphs...");
        List<Flight> allFlights = inventoryServiceAccessor.getAllFlights();
        
        costGraph = new FlightGraph();
        durationGraph = new FlightGraph();
        
        for (Flight flight : allFlights) {
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
        }
        
        logger.info("Graphs pre-computed successfully! Cities: {}, Flights: {}", 
                   costGraph.getCities().size(), allFlights.size());
    }

    public FlightGraph getCostGraph() {
        return costGraph;
    }

    public FlightGraph getDurationGraph() {
        return durationGraph;
    }
}