package org.diagnosis.evaluation.metrics.full_matching_metrics;

import org.diagnosis.algorithms.entities.Component;
import org.diagnosis.algorithms.entities.HitVector;
import org.diagnosis.algorithms.reconstruction.graph.ExecutionGraph;
import org.diagnosis.evaluation.Metric;
import org.diagnosis.evaluation.metrics.matching.Precision;

import java.util.Set;

// The F1-score is the harmonic mean of precision and recall, providing a balance between the two. An F1-score reaches its best value at 1 (perfect precision and recall) and the worst at 0.
public class PrecisionFull extends Metric {

    Precision precision;
    ScopeType scopeType;

    public PrecisionFull(ScopeType scopeType, AlgorithmType algorithm, ExecutionGraph executionGraph, HitVector fullHitVector, HitVector baselineHitVector, HitVector reconstructedHitVector, HitVector weightedReconstructedHitVector) {
        super();
        this.scopeType = scopeType;

        HitVector actualExecutionTrace = fullHitVector;
        HitVector predictedExecutionTrace = null;

        if (algorithm == AlgorithmType.PartialTrace) {
            predictedExecutionTrace = baselineHitVector;
        } else if (algorithm == AlgorithmType.ReconstructedTrace) {
            predictedExecutionTrace = reconstructedHitVector;
        } else if (algorithm == AlgorithmType.WeightedReconstructedTrace) {
            predictedExecutionTrace = weightedReconstructedHitVector;
        } else {
            throw new IllegalArgumentException("Invalid algorithm: " + algorithm);
        }

        if (scopeType == ScopeType.ExecutionGraph) {
            actualExecutionTrace = actualExecutionTrace.filterFromExecutionGraph(executionGraph);
            predictedExecutionTrace = predictedExecutionTrace.filterFromExecutionGraph(executionGraph);
        } else if (scopeType == ScopeType.Missing) {
            Set<Component> filteredComponents = predictedExecutionTrace.getFilteredComponents();
            actualExecutionTrace = actualExecutionTrace.filterComponentsToKeep(filteredComponents);
            predictedExecutionTrace = predictedExecutionTrace.filterComponentsToKeep(filteredComponents);
        }
        this.precision = new Precision(actualExecutionTrace, predictedExecutionTrace);
    }

    @Override
    public double calculate() {
        return precision.calculate();
    }

    @Override
    public String getName() {
        return "Precision" + this.scopeType.name();
    }


}
