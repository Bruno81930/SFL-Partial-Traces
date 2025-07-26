package org.diagnosis.evaluation.metrics.matching;

import org.diagnosis.algorithms.entities.HitVector;

// Specificity measures the proportion of actual negatives that are correctly identified as negatives.
public class Specificity extends ConfusionMatrix {

    public Specificity(HitVector actualExecutionTrace, HitVector predictedExecutionTrace) {
        super(actualExecutionTrace, predictedExecutionTrace);
    }

    @Override
    public double calculate() {
        if (getTrueNegatives() + getFalsePositives() == 0) {
            return 0;
        }
        return getTrueNegatives() / (double) (getTrueNegatives() + getFalsePositives());
    }

    @Override
    public String getName() {
        return "Specificity";
    }


}
