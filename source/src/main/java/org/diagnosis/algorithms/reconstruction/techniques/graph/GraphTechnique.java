package org.diagnosis.algorithms.reconstruction.techniques.graph;

import org.diagnosis.algorithms.entities.Component;
import org.diagnosis.algorithms.entities.HitVector;
import org.diagnosis.algorithms.reconstruction.graph.ExecutionGraph;
import org.diagnosis.algorithms.reconstruction.graph.nodes.EndNode;
import org.diagnosis.algorithms.reconstruction.graph.nodes.Node;
import org.diagnosis.algorithms.reconstruction.graph.nodes.NopNode;
import org.diagnosis.algorithms.reconstruction.graph.nodes.StartNode;
import org.diagnosis.algorithms.reconstruction.techniques.Technique;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GraphTechnique implements Technique {

    final ExecutionGraph executionGraph;

    public GraphTechnique(ExecutionGraph executionGraph) {
        this.executionGraph = executionGraph;
    }

    @Override
    public HitVector reconstruct(HitVector partialTrace) {
        executionGraph.setPartialTraces(partialTrace);
        Set<Component> partialTracesComponents = partialTrace.getComponents();

        Set<Component> filteredComponents = partialTrace.getFilteredComponents();


        Map<Boolean, List<Node>> nodesInPartialTracesMap = executionGraph.nodes.values().stream()
                .filter(node -> !(node instanceof NopNode) && !(node instanceof StartNode) && !(node instanceof EndNode))
                .collect(Collectors.partitioningBy(node -> partialTracesComponents.contains(node.toComponent())));
        List<Node> nodesNotInPartialTraces = nodesInPartialTracesMap.get(false).stream()
                .filter(node -> filteredComponents.contains(node.toComponent()))
                .collect(Collectors.toList());


        HitVector reconstructedTrace = partialTrace.clone();
        for (Node node : nodesNotInPartialTraces) {
            this.extracted(executionGraph, nodesInPartialTracesMap.get(true), reconstructedTrace, node);
        }

        executionGraph.setReconstructedTrace(reconstructedTrace);

        return reconstructedTrace;
    }

    public void extracted(ExecutionGraph executionGraph, List<Node> nodesInPartialTraces, HitVector reconstructedTrace, Node node) {
        executionGraph.remove(node);
        ConnectivityInspector<String, DefaultEdge> connectivityInspector = new ConnectivityInspector<>(executionGraph.graph);
        connectivityInspector.isConnected();
        for (Node node1 : nodesInPartialTraces) {
            for (Node node2 : nodesInPartialTraces) {
                if (!connectivityInspector.pathExists(node1.toString(), node2.toString())) {
                    reconstructedTrace.add(node.createHit());
                    executionGraph.rollBack();
                    return;
                }
            }
        }
        executionGraph.commit();
    }

    public boolean isNodeCovered(Node node, HitVector partialTrace) {
        Map<Boolean, List<Node>> nodesInPartialTracesMap = executionGraph.nodes.values().stream()
                .filter(n -> !(n instanceof NopNode) && !(n instanceof StartNode) && !(n instanceof EndNode))
                .collect(Collectors.partitioningBy(n -> partialTrace.getComponents().contains(n.toComponent())));
        List<Node> nodesInPartialTraces = nodesInPartialTracesMap.get(true);
        executionGraph.remove(node);
        ConnectivityInspector<String, DefaultEdge> connectivityInspector = new ConnectivityInspector<>(executionGraph.graph);
        connectivityInspector.isConnected();
        for (Node node1 : nodesInPartialTraces) {
            for (Node node2 : nodesInPartialTraces) {
                if (!connectivityInspector.pathExists(node1.toString(), node2.toString())) {
                    executionGraph.rollBack();
                    return true;
                }
            }
        }
        executionGraph.rollBack();
        return false;
    }

    public float getGraphCoverage(Node node, HitVector hitVector) {
        int totalPaths = countPaths(executionGraph.graph, executionGraph.startNode.toString(), executionGraph.endNode.toString());
        executionGraph.remove(node);
        int coveredPaths = countPaths(executionGraph.graph, executionGraph.startNode.toString(), executionGraph.endNode.toString());
        executionGraph.rollBack();
        return (float) coveredPaths / totalPaths;
    }

    private int countPaths(Graph<String, DefaultEdge> dag, String startVertex, String endVertex) {
        // Perform a topological sort of the DAG
        TopologicalOrderIterator<String, DefaultEdge> orderIterator = new TopologicalOrderIterator<>(dag);

        // Initialize path counts
        Map<String, Integer> pathCounts = new HashMap<>();
        dag.vertexSet().forEach(vertex -> pathCounts.put(vertex, 0));
        // There is one path to startVertex itself
        pathCounts.put(startVertex, 1);

        // Iterate in topological order to count paths
        while (orderIterator.hasNext()) {
            String currentVertex = orderIterator.next();
            for (DefaultEdge edge : dag.outgoingEdgesOf(currentVertex)) {
                String successor = dag.getEdgeTarget(edge);
                pathCounts.put(successor, pathCounts.get(successor) + pathCounts.get(currentVertex));
            }
        }

        return pathCounts.getOrDefault(endVertex, 0);
    }
}
