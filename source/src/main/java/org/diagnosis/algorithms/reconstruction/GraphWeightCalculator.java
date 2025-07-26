package org.diagnosis.algorithms.reconstruction;

import org.diagnosis.algorithms.entities.Component;
import org.diagnosis.algorithms.reconstruction.graph.nodes.Node;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class GraphWeightCalculator {

    public static Map<DefaultEdge, Integer> calculateWeightsRecursive(DirectedAcyclicGraph<String, DefaultEdge> graph, String endNode, Set<Component> partialComponents, Map<String, Node> nodes) {
        Map<DefaultEdge, Integer> weights = new HashMap<>();
        computeWeights(graph, endNode, 0, weights, partialComponents, nodes);
        return weights;
    }

    private static void computeWeights(DirectedAcyclicGraph<String, DefaultEdge> graph, String node, int count, Map<DefaultEdge, Integer> weights, Set<Component> partialComponents, Map<String, Node> nodes) {
        if (partialComponents.contains(nodes.get(node).toComponent())) {
            count++;
        }

        for (DefaultEdge edge : graph.incomingEdgesOf(node)) {
            String source = graph.getEdgeSource(edge);
            weights.put(edge, count);
            computeWeights(graph, source, count, weights, partialComponents, nodes);
        }
    }

    public static Map<DefaultEdge, Integer> calculateWeights(DirectedAcyclicGraph<String, DefaultEdge> graph, String endNode, Set<Component> partialComponents, Map<String, Node> nodes) {
        Map<DefaultEdge, Integer> weights = new HashMap<>();
        Stack<String> stack = new Stack<>();
        Map<String, Integer> nodeCounts = new HashMap<>();

        stack.push(endNode);
        nodeCounts.put(endNode, 0);

        while (!stack.isEmpty()) {
            String currentNode = stack.pop();
            int count = nodeCounts.get(currentNode);

            Node node = nodes.get(currentNode);
            if (partialComponents.contains(node.toComponent())) {
                count++;
            }

            for (DefaultEdge edge : graph.incomingEdgesOf(currentNode)) {
                String source = graph.getEdgeSource(edge);
                weights.put(edge, count);

                if (!nodeCounts.containsKey(source) || nodeCounts.get(source) < count) {
                    stack.push(source);
                    nodeCounts.put(source, count);
                }
            }
        }

        return weights;
    }
}

