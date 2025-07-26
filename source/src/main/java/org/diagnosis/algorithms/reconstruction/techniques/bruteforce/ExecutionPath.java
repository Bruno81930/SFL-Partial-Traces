package org.diagnosis.algorithms.reconstruction.techniques.bruteforce;

import org.diagnosis.algorithms.entities.Component;
import org.diagnosis.algorithms.entities.Hit;
import org.diagnosis.algorithms.entities.HitVector;
import org.diagnosis.algorithms.reconstruction.graph.nodes.Node;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.ExportException;
import org.jgrapht.nio.dot.DOTExporter;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExecutionPath {
    List<String> path;
    Map<String, Node> nodes;
    Graph<String, DefaultEdge> graph;

    public ExecutionPath(List<String> path, Map<String, Node> nodes, Graph<String, DefaultEdge> graph) {
        this.path = path;
        this.nodes = nodes;
        this.graph = graph;
    }

    public List<Component> getComponents() {
        List<Component> components = new ArrayList<>();
        for (String node : path) {
            components.add(nodes.get(node).createHit().getComponent());
        }
        return components;
    }

    @Override
    public String toString() {
        // Export the graph to DOT format with custom attributes
        DOTExporter<String, DefaultEdge> exporter = new DOTExporter<>();
        exporter.setVertexAttributeProvider( s -> {
                    Map<String, Attribute> map = new HashMap<>();
                    map.put("label", DefaultAttribute.createAttribute(s));
                    if (path.contains(s)) {
                        map.put("color", DefaultAttribute.createAttribute("red"));  // Highlight visited vertices in red
                    }
                    return map;
                }
        );

        StringWriter writer = new StringWriter();
        try {
            exporter.exportGraph(graph, writer);
        } catch (ExportException e) {
            return graph.toString();
        }
        return writer.toString();
    }

    public HitVector fillHitVector(HitVector hitVector) {
        Map<Component, Float> newHitVectorMap = hitVector.getClone();
        HitVector newHitVector;
        for (String node : path) {
            Hit hit = nodes.get(node).createHit();
            if (newHitVectorMap.containsKey(hit.getComponent())) {
                newHitVectorMap.put(hit.getComponent(), 1.0f);
            }
        }
        newHitVector = new HitVector(newHitVectorMap);
        newHitVector.fillPartialHits();
        return newHitVector;
    }

    public int size() {
        return path.size();
    }
}
