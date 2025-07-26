package org.diagnosis.algorithms.reconstruction.techniques.probabilities;

import org.diagnosis.algorithms.entities.Component;
import org.diagnosis.algorithms.entities.Hit;
import org.diagnosis.algorithms.entities.HitVector;
import org.diagnosis.algorithms.reconstruction.graph.Counter;
import org.diagnosis.algorithms.reconstruction.graph.ExecutionGraph;
import org.diagnosis.algorithms.reconstruction.graph.nodes.Node;
import org.diagnosis.algorithms.reconstruction.techniques.graph.GraphTechnique;
import org.diagnosis.algorithms.walker.InformedRandomWalkStrategy;
import org.diagnosis.algorithms.walker.InformedWalkStrategy;
import org.diagnosis.algorithms.walker.RandomWalkStrategy;
import org.diagnosis.algorithms.walker.WalkStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProbabilitiesTechnique extends GraphTechnique {

    private final int randomWalks;
    private final WalkStrategy walkStrategy;
    public ProbabilitiesTechnique (ExecutionGraph executionGraph) {
        this(executionGraph, 1000, new RandomWalkStrategy());
    }

    public ProbabilitiesTechnique(ExecutionGraph executionGraph, int randomWalks) {
        this(executionGraph, randomWalks, new RandomWalkStrategy());
    }

    public ProbabilitiesTechnique(ExecutionGraph executionGraph, int randomWalks, WalkStrategy walkStrategy) {
        super(executionGraph);
        this.randomWalks = randomWalks;
        this.walkStrategy = walkStrategy;
    }

    @Override
    public void extracted(ExecutionGraph executionGraph, List<Node> nodesInPartialTraces, HitVector reconstructedTrace, Node node) {
        if (!executionGraph.isComponentInGraph(node.toComponent())) {
            reconstructedTrace.add(node.createMiss());
            return;
        }

        List<Component> componentsInPartialTraces = nodesInPartialTraces.stream().map(Node::toComponent).collect(Collectors.toList());
        int p = 1;
        List<Counter> counters = new ArrayList<>();
        for (int i = 0; i < randomWalks; i++) {
            boolean wasCaptured;
            Counter counter = new Counter();
            if (walkStrategy instanceof InformedRandomWalkStrategy) {
                wasCaptured = executionGraph.checkNodeInInformedRandomWalk(node, ((InformedRandomWalkStrategy) walkStrategy).getModifier(), componentsInPartialTraces, counter);
            } else if (walkStrategy instanceof InformedWalkStrategy) {
                wasCaptured = executionGraph.checkNodeInInformedWalk(node, componentsInPartialTraces, counter);
            } else if (walkStrategy instanceof RandomWalkStrategy) {
                wasCaptured = executionGraph.checkNodeInRandomWalk(node, componentsInPartialTraces, counter);
            } else {
                throw new UnsupportedOperationException("Unsupported walk strategy");
            }

            if (wasCaptured) {
                p++;
            }
            counters.add(counter);
        }

        Hit hit = node.createHit();
        hit.setHitPercentage((float) p / (randomWalks + 1));
        hit.setAverageCounterForPartialComponentsInRandomWalks(counters.stream().mapToDouble(Counter::getCount).average().orElse(Double.NaN));
        reconstructedTrace.addPercentagesFromHit(hit);
        reconstructedTrace.addAverageCounterForPartialComponentsInRandomWalksFromHit(hit);
    }
}
