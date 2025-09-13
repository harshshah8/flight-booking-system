package com.fbs.search.service;

import com.fbs.search.model.FlightEdge;
import com.fbs.search.model.FlightGraph;
import com.fbs.search.model.FlightPath;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class FlightSearchAlgorithm {

    public List<FlightPath> findKShortestPaths(FlightGraph graph, String source, String destination, 
                                             int k, boolean sortByCost) {
        if (k <= 0) return new ArrayList<>();
        
        List<FlightPath> kPaths = new ArrayList<>();
        PriorityQueue<FlightPath> candidates = new PriorityQueue<>(
            sortByCost ? Comparator.comparing(FlightPath::getTotalCost) : 
                        Comparator.comparing(FlightPath::getTotalDuration)
        );
        
        // Find shortest path first (Dijkstra-like)
        FlightPath shortestPath = dijkstraShortestPath(graph, source, destination, sortByCost);
        if (shortestPath != null) {
            kPaths.add(shortestPath);
            if (k == 1) return kPaths;
        } else {
            // No path found at all
            return kPaths; // Return empty list
        }
        
        // Yen's algorithm for k-1 additional paths
        for (int i = 1; i < k; i++) {
            if (i - 1 >= kPaths.size()) break; // Safety check
            FlightPath lastPath = kPaths.get(i - 1);
            
            // For each node in the previous k-shortest path except the last
            for (int j = 0; j < lastPath.getFlights().size(); j++) {
                String spurNode = (j == 0) ? source : lastPath.getFlights().get(j - 1).getDestination();
                List<FlightEdge> rootPath = lastPath.getFlights().subList(0, j);
                
                // Create modified graph by removing conflicting edges
                FlightGraph modifiedGraph = createModifiedGraph(graph, kPaths, rootPath, spurNode);
                
                // Find spur path from spurNode to destination
                FlightPath spurPath = dijkstraShortestPath(modifiedGraph, spurNode, destination, sortByCost);
                
                if (spurPath != null) {
                    // Combine root path + spur path
                    List<FlightEdge> totalPath = new ArrayList<>(rootPath);
                    totalPath.addAll(spurPath.getFlights());
                    candidates.offer(new FlightPath(totalPath));
                }
            }
            
            if (candidates.isEmpty()) break;
            
            FlightPath nextPath = candidates.poll();
            if (!isDuplicate(kPaths, nextPath)) {
                kPaths.add(nextPath);
            } else {
                i--; // Try again with next candidate
            }
        }
        
        return kPaths;
    }
    
    private FlightPath dijkstraShortestPath(FlightGraph graph, String source, String destination, boolean sortByCost) {
        Map<String, Double> distances = new HashMap<>();
        Map<String, FlightPath> paths = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparing(n -> n.distance));
        Set<String> visited = new HashSet<>();
        
        // Initialize
        for (String city : graph.getCities()) {
            distances.put(city, Double.MAX_VALUE);
        }
        distances.put(source, 0.0);
        pq.offer(new Node(source, 0.0));
        paths.put(source, new FlightPath(new ArrayList<>()));
        
        while (!pq.isEmpty()) {
            Node current = pq.poll();
            if (visited.contains(current.city)) continue;
            visited.add(current.city);
            
            if (current.city.equals(destination)) {
                return paths.get(destination);
            }
            
            for (FlightEdge edge : graph.getEdges(current.city)) {
                String neighbor = edge.getDestination();
                double edgeWeight = sortByCost ? edge.getCost().doubleValue() : edge.getDuration().doubleValue();
                double newDistance = distances.get(current.city) + edgeWeight;
                
                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    
                    // Build path
                    List<FlightEdge> newPath = new ArrayList<>(paths.get(current.city).getFlights());
                    newPath.add(edge);
                    paths.put(neighbor, new FlightPath(newPath));
                    
                    pq.offer(new Node(neighbor, newDistance));
                }
            }
        }
        
        return null; // No path found
    }
    
    private FlightGraph createModifiedGraph(FlightGraph original, List<FlightPath> kPaths, 
                                          List<FlightEdge> rootPath, String spurNode) {
        FlightGraph modified = new FlightGraph();
        
        // Copy all edges
        for (String city : original.getCities()) {
            for (FlightEdge edge : original.getEdges(city)) {
                modified.addEdge(edge);
            }
        }
        
        // Remove edges that would create duplicate paths
        for (FlightPath path : kPaths) {
            if (path.getFlights().size() > rootPath.size()) {
                List<FlightEdge> pathEdges = path.getFlights();
                boolean matchesRoot = true;
                for (int i = 0; i < rootPath.size(); i++) {
                    if (!pathEdges.get(i).equals(rootPath.get(i))) {
                        matchesRoot = false;
                        break;
                    }
                }
                
                if (matchesRoot && pathEdges.size() > rootPath.size()) {
                    // Remove the edge that would continue this existing path
                    FlightEdge toRemove = pathEdges.get(rootPath.size());
                    removeEdgeFromGraph(modified, toRemove);
                }
            }
        }
        
        return modified;
    }
    
    private void removeEdgeFromGraph(FlightGraph graph, FlightEdge edgeToRemove) {
        List<FlightEdge> edges = graph.getEdges(edgeToRemove.getSource());
        edges.removeIf(edge -> edge.getFlightId().equals(edgeToRemove.getFlightId()));
    }
    
    private boolean isDuplicate(List<FlightPath> existingPaths, FlightPath newPath) {
        for (FlightPath existing : existingPaths) {
            if (pathsEqual(existing, newPath)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean pathsEqual(FlightPath path1, FlightPath path2) {
        List<FlightEdge> flights1 = path1.getFlights();
        List<FlightEdge> flights2 = path2.getFlights();
        
        if (flights1.size() != flights2.size()) return false;
        
        for (int i = 0; i < flights1.size(); i++) {
            if (!flights1.get(i).getFlightId().equals(flights2.get(i).getFlightId())) {
                return false;
            }
        }
        return true;
    }
    
    public List<FlightPath> findCheapestPaths(FlightGraph graph, String source, String destination, int k) {
        return findKShortestPaths(graph, source, destination, k, true);
    }
    
    public List<FlightPath> findFastestPaths(FlightGraph graph, String source, String destination, int k) {
        return findKShortestPaths(graph, source, destination, k, false);
    }
    
    private static class Node {
        String city;
        double distance;
        
        Node(String city, double distance) {
            this.city = city;
            this.distance = distance;
        }
    }
}