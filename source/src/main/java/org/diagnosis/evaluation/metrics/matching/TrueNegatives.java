package org.diagnosis.evaluation.metrics.matching;

import org.diagnosis.algorithms.entities.HitVector;

// The proportion of positive identifications that were false
public class TrueNegatives extends ConfusionMatrix {

    public TrueNegatives(HitVector actualExecutionTrace, HitVector predictedExecutionTrace) {
        super(actualExecutionTrace, predictedExecutionTrace);
    }

    @Override
    public double calculate() {
        return getTrueNegatives();
    }

    @Override
    public String getName() {
        return "TrueNegatives";
    }


}
