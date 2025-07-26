package org.diagnosis.evaluation;

import org.diagnosis.algorithms.entities.HitVector;
import org.diagnosis.evaluation.ground_truth.GroundTruth;
import org.diagnosis.evaluation.metrics.matching.*;

public class MatchingMetrics extends Metrics {

    public MatchingMetrics(GroundTruth groundTruth, HitVector actualExecutionTrace, HitVector predictedExecutionTrace) {
        super();
        this.metrics.add(new Accuracy(actualExecutionTrace, predictedExecutionTrace));
        this.metrics.add(new F1Score(actualExecutionTrace, predictedExecutionTrace));
        this.metrics.add(new FalseDiscoveryRate(actualExecutionTrace, predictedExecutionTrace));
        this.metrics.add(new FalsePositiveRate(actualExecutionTrace, predictedExecutionTrace));
        this.metrics.add(new MissRate(actualExecutionTrace, predictedExecutionTrace));
        this.metrics.add(new NegativePredictiveValue(actualExecutionTrace, predictedExecutionTrace));
        this.metrics.add(new Precision(actualExecutionTrace, predictedExecutionTrace));
        this.metrics.add(new Recall(actualExecutionTrace, predictedExecutionTrace));
        this.metrics.add(new Specificity(actualExecutionTrace, predictedExecutionTrace));
        this.metrics.add(new TruePositives(actualExecutionTrace, predictedExecutionTrace));
        this.metrics.add(new TrueNegatives(actualExecutionTrace, predictedExecutionTrace));
        this.metrics.add(new FalsePositives(actualExecutionTrace, predictedExecutionTrace));
        this.metrics.add(new FalseNegatives(actualExecutionTrace, predictedExecutionTrace));
        this.metrics.add(new GroundTruthHits(groundTruth, actualExecutionTrace, "Actual"));
        this.metrics.add(new GroundTruthHits(groundTruth, predictedExecutionTrace, "Predicted"));

    }
}
