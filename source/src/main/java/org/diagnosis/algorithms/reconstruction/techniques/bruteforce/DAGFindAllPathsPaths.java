package org.diagnosis.algorithms.reconstruction.techniques.bruteforce;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class DAGFindAllPathsPaths {
    public static List<List<String>> findAllShortestPaths(Graph<String, DefaultEdge> graph, String startVertex, String endVertex) {
        Map<String, List<Deque<String>>> paths = new HashMap<>();
        List<Deque<String>> startPaths = new ArrayList<>();
        Deque<String> startPath = new LinkedList<>();

        startPath.add(startVertex);
        startPaths.add(startPath);
        paths.put(startVertex, startPaths);

        for (String vertex : (DirectedAcyclicGraph<String, DefaultEdge>) graph) {
            if (!vertex.equals(startVertex)) {
                List<Deque<String>> newPaths = new ArrayList<>();
                for (DefaultEdge edge : graph.incomingEdgesOf(vertex)) {
                    String sourceVertex = graph.getEdgeSource(edge);
                    if (paths.containsKey(sourceVertex) && !paths.get(sourceVertex).isEmpty()) {
                        for (Deque<String> existingPath : paths.get(sourceVertex)) {
                            Deque<String> newPath = new LinkedList<>(existingPath);
                            newPath.add(vertex);
                            newPaths.add(newPath);
                        }
                    }
                    if (!newPaths.isEmpty()) {
                        paths.put(vertex, newPaths);
                    }
                }
            }
        }
        List<List<String>> result = new ArrayList<>();
        if (paths.containsKey(endVertex)) {
            for (Deque<String> path : paths.get(endVertex)) {
                result.add(new ArrayList<>(path));
            }
        }
        return result;
    }

    public static List<List<String>> findAllShortestPathsMemoryEfficient(Graph<String, DefaultEdge> graph, String startVertex, String endVertex) {
        Logger logger = LoggerFactory.getLogger(DAGFindAllPathsPaths.class);
        logger.info("Finding all shortest paths from graph: {} nodes", graph.vertexSet().size());
        // Map to store the previous node for each node
        Map<String, List<String>> predecessors = new HashMap<>();

        // Initialize the map with empty lists
        for (String vertex : graph.vertexSet()) {
            predecessors.put(vertex, new ArrayList<>());
        }

        // Populate the map with actual predecessors
        for (String vertex : graph.vertexSet()) {
            for (DefaultEdge edge : graph.incomingEdgesOf(vertex)) {
                String sourceVertex = graph.getEdgeSource(edge);
                predecessors.get(vertex).add(sourceVertex);
            }
        }

        // Iteratively build paths from endVertex to startVertex
        List<List<String>> allPaths = new ArrayList<>();
        Deque<List<String>> stack = new ArrayDeque<>();
        stack.push(Collections.singletonList(endVertex));

        while (!stack.isEmpty()) {
            logger.info("Number of Paths: {} <--> Size of Stack: {}", allPaths.size(), stack.size());
            List<String> currentPath = stack.pop();
            String lastVertex = currentPath.get(0);

            if (lastVertex.equals(startVertex)) {
                allPaths.add(new ArrayList<>(currentPath));
                continue;
            }

            for (String pred : predecessors.get(lastVertex)) {
                List<String> newPath = new ArrayList<>(currentPath);
                newPath.add(0, pred); // Add predecessor at the beginning of the list
                stack.push(newPath);
            }
        }

        System.gc();
        return allPaths;
    }

    public static List<List<String>> findAllShortestPathsWithStreams(Graph<String, DefaultEdge> graph, String startVertex, String endVertex) {
        Map<String, List<Deque<String>>> paths = new HashMap<>();
        paths.put(startVertex, Collections.singletonList(new LinkedList<>(Collections.singletonList(startVertex))));

        graph.vertexSet().stream().filter(vertex -> !vertex.equals(startVertex)).forEach(vertex -> {
            List<Deque<String>> newPaths = graph.incomingEdgesOf(vertex).stream()
                    .flatMap(edge -> {
                        String sourceVertex = graph.getEdgeSource(edge);
                        return paths.getOrDefault(sourceVertex, Collections.emptyList()).stream()
                                .map(existingPath -> {
                                    Deque<String> newPath = new LinkedList<>(existingPath);
                                    newPath.addLast(vertex);
                                    return newPath;
                                });
                    })
                    .collect(Collectors.toList());

            if (!newPaths.isEmpty()) {
                paths.put(vertex, newPaths);
            }
        });

        return paths.getOrDefault(endVertex, Collections.emptyList()).stream()
                .map(ArrayList::new)
                .collect(Collectors.toList());
    }

}
