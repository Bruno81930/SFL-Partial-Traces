package org.diagnosis.evaluation.metrics.matching;

import org.diagnosis.algorithms.entities.HitVector;

// The proportion of positive identifications that were false
public class FalsePositives extends ConfusionMatrix {

    public FalsePositives(HitVector actualExecutionTrace, HitVector predictedExecutionTrace) {
        super(actualExecutionTrace, predictedExecutionTrace);
    }

    @Override
    public double calculate() {
        return getFalsePositives();
    }

    @Override
    public String getName() {
        return "FalsePositives";
    }


}
