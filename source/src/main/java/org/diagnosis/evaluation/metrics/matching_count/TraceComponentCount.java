package org.diagnosis.evaluation.metrics.matching_count;

import org.diagnosis.algorithms.entities.Component;
import org.diagnosis.algorithms.entities.Hit;
import org.diagnosis.algorithms.entities.HitVector;
import org.diagnosis.algorithms.reconstruction.graph.ExecutionGraph;
import org.diagnosis.algorithms.reconstruction.graph.nodes.Node;
import org.diagnosis.evaluation.Metric;
import org.diagnosis.evaluation.metrics.matching_count.types.Algorithm;
import org.diagnosis.evaluation.metrics.matching_count.types.ComponentSet;
import org.diagnosis.evaluation.metrics.matching_count.types.HitType;

import java.util.Set;
import java.util.stream.Collectors;

public class TraceComponentCount extends Metric {

    Algorithm algorithm;
    HitType hitType;
    ComponentSet componentSet;

    Set<Component> traceComponents;

    public TraceComponentCount(ExecutionGraph executionGraph, HitVector hitVector, Algorithm algorithm, HitType hitType, ComponentSet componentSet) {
        this.algorithm = algorithm;
        this.hitType = hitType;
        this.componentSet = componentSet;

        if (hitType == HitType.Hit) {
            this.traceComponents = hitVector.getHitComponents();
        } else if (hitType == HitType.Miss){
            this.traceComponents = hitVector.stream().filter(Hit::isMiss).map(Hit::getComponent).collect(Collectors.toSet());
        } else {
            this.traceComponents = hitVector.stream().filter(hit -> hit.getNumberOfHits() == -1).map(Hit::getComponent).collect(Collectors.toSet());
        }

        if (componentSet == ComponentSet.ExecutionGraph) {
            Set<Component> executionGraphComponents = executionGraph.nodes.values().stream().map(Node::toComponent).collect(Collectors.toSet());
            this.traceComponents = this.traceComponents.stream().filter(executionGraphComponents::contains).collect(Collectors.toSet());
        } else if (componentSet == ComponentSet.NotExecutionGraph) {
            Set<Component> executionGraphComponents = executionGraph.nodes.values().stream().map(Node::toComponent).collect(Collectors.toSet());
            this.traceComponents = this.traceComponents.stream().filter(component -> !executionGraphComponents.contains(component)).collect(Collectors.toSet());
        }
    }

    @Override
    public double calculate() {
        return traceComponents.size();
    }

    @Override
    public String getName() {
        return hitType.name() + componentSet.name() + algorithm.name() + "Count";
    }


}
