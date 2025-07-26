package org.diagnosis.evaluation.metrics.matching;

import org.diagnosis.algorithms.entities.HitVector;

// The proportion of positive identifications that were false
public class FalseNegatives extends ConfusionMatrix {

    public FalseNegatives(HitVector actualExecutionTrace, HitVector predictedExecutionTrace) {
        super(actualExecutionTrace, predictedExecutionTrace);
    }

    @Override
    public double calculate() {
        return getFalseNegatives();
    }

    @Override
    public String getName() {
        return "FalseNegatives";
    }


}
