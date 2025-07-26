package org.diagnosis.algorithms.reconstruction.techniques.graph;

import org.diagnosis.algorithms.entities.Hit;
import org.diagnosis.algorithms.entities.HitVector;
import org.diagnosis.algorithms.reconstruction.graph.ExecutionGraph;
import org.diagnosis.algorithms.reconstruction.graph.nodes.Node;

import java.util.List;

public class GraphWeightedTechnique extends GraphTechnique{
    public GraphWeightedTechnique(ExecutionGraph executionGraph, HitVector hitVector) {
        super(executionGraph);
        executionGraph.calculateWeights(hitVector);
        executionGraph.calculateNodeProbabilities();
    }

    @Override
    public void extracted(ExecutionGraph executionGraph, List<Node> nodesInPartialTraces, HitVector reconstructedTrace, Node node) {
        reconstructedTrace.addPercentagesFromHit(new Hit(node.toComponent(), executionGraph.getWeightProbability(node)));
        //float probability = executionGraph.getWeightProbability(node);
        //if (probability == 1) {
        //    reconstructedTrace.add(new Hit(node.toComponent(), 1));
        //} else {
        //    if (reconstructedTrace.getHits(node.toComponent()) == -1) {
        //        reconstructedTrace.add(new Hit(node.toComponent(), 0));
        //    }
        //}
    }
}
