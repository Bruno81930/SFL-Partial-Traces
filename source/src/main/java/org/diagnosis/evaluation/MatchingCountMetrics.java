package org.diagnosis.evaluation;

import org.diagnosis.algorithms.entities.HitVector;
import org.diagnosis.algorithms.reconstruction.graph.ExecutionGraph;
import org.diagnosis.evaluation.ground_truth.GroundTruth;
import org.diagnosis.evaluation.metrics.matching.*;
import org.diagnosis.evaluation.metrics.matching_count.TraceComponentCount;
import org.diagnosis.evaluation.metrics.matching_count.types.Algorithm;
import org.diagnosis.evaluation.metrics.matching_count.types.ComponentSet;
import org.diagnosis.evaluation.metrics.matching_count.types.HitType;

public class MatchingCountMetrics extends Metrics {

    public MatchingCountMetrics(ExecutionGraph executionGraph, HitVector fullTrace, HitVector partialTrace, HitVector reconstructedTrace, HitVector weightedReconstructedTrace) {
        super();
        for (HitType hitType : HitType.values()) {
            for (ComponentSet componentSet : ComponentSet.values()) {
                this.metrics.add(new TraceComponentCount(executionGraph, fullTrace, Algorithm.FullTrace, hitType, componentSet));
                this.metrics.add(new TraceComponentCount(executionGraph, partialTrace, Algorithm.PartialTrace, hitType, componentSet));
                this.metrics.add(new TraceComponentCount(executionGraph, reconstructedTrace, Algorithm.ReconstructedTrace, hitType, componentSet));
                this.metrics.add(new TraceComponentCount(executionGraph, weightedReconstructedTrace, Algorithm.WeightedReconstructedTrace, hitType, componentSet));
            }
        }
    }
}
