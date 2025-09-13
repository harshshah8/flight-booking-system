package com.fbs.search.model;

import java.math.BigDecimal;
import java.util.*;

public class FlightGraph {
    private Map<String, List<FlightEdge>> adjacencyList;
    private Set<String> cities;

    public FlightGraph() {
        this.adjacencyList = new HashMap<>();
        this.cities = new HashSet<>();
    }

    public void addEdge(FlightEdge edge) {
        adjacencyList.computeIfAbsent(edge.getSource(), k -> new ArrayList<>()).add(edge);
        cities.add(edge.getSource());
        cities.add(edge.getDestination());
    }

    public List<FlightEdge> getEdges(String city) {
        return adjacencyList.getOrDefault(city, new ArrayList<>());
    }

    public Set<String> getCities() {
        return cities;
    }

    public boolean hasCity(String city) {
        return cities.contains(city);
    }
}