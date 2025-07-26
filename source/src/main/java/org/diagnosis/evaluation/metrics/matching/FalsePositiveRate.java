package org.diagnosis.evaluation.metrics.matching;

import org.diagnosis.algorithms.entities.HitVector;

// It measures the proportion of actual negatives that are incorrectly identified as positives.
public class FalsePositiveRate extends ConfusionMatrix {

    public FalsePositiveRate(HitVector actualExecutionTrace, HitVector predictedExecutionTrace) {
        super(actualExecutionTrace, predictedExecutionTrace);
    }

    @Override
    public double calculate() {
        if (getTrueNegatives() + getFalsePositives() == 0) {
            return 1;
        }
        return 1 - (getTrueNegatives() / (double) (getTrueNegatives() + getFalsePositives()));
    }

    @Override
    public String getName() {
        return "FalsePositiveRate";
    }


}
