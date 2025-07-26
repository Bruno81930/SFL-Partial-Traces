package org.diagnosis.evaluation;

import org.diagnosis.algorithms.entities.HitVector;
import org.diagnosis.algorithms.reconstruction.graph.ExecutionGraph;
import org.diagnosis.evaluation.metrics.full_matching_metrics.ScopeType;
import org.diagnosis.evaluation.metrics.full_matching_metrics.*;

public class FullMatchingMetrics extends Metrics {

    public FullMatchingMetrics(ExecutionGraph executionGraph, AlgorithmType algorithm, HitVector fullHitVector, HitVector baselineHitVector, HitVector reconstructedHitVector, HitVector weightedReconstructedHitVector) {
        super();
        for (ScopeType scopeType : ScopeType.values()) {
            this.metrics.add(new F1ScoreFull(scopeType, algorithm, executionGraph, fullHitVector, baselineHitVector, reconstructedHitVector, weightedReconstructedHitVector));
            this.metrics.add(new PrecisionFull(scopeType, algorithm, executionGraph, fullHitVector, baselineHitVector, reconstructedHitVector, weightedReconstructedHitVector));
            this.metrics.add(new RecallFull(scopeType, algorithm, executionGraph, fullHitVector, baselineHitVector, reconstructedHitVector, weightedReconstructedHitVector));
        }
    }
}
