package org.diagnosis.evaluation.metrics.matching;

import org.diagnosis.algorithms.entities.HitVector;

// The proportion of positive identifications that were false
public class FalseDiscoveryRate extends ConfusionMatrix {

    public FalseDiscoveryRate(HitVector actualExecutionTrace, HitVector predictedExecutionTrace) {
        super(actualExecutionTrace, predictedExecutionTrace);
    }

    @Override
    public double calculate() {
        if (getTruePositives() + getFalsePositives() == 0) {
            return 0;
        }
        return getFalsePositives() / (double) (getFalsePositives() + getTruePositives());
    }

    @Override
    public String getName() {
        return "FalseDiscoveryRate";
    }


}
