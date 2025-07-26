package org.diagnosis.evaluation.metrics.matching;

import org.diagnosis.algorithms.entities.HitVector;

// The proportion of positive identifications that were false
public class TruePositives extends ConfusionMatrix {

    public TruePositives(HitVector actualExecutionTrace, HitVector predictedExecutionTrace) {
        super(actualExecutionTrace, predictedExecutionTrace);
    }

    @Override
    public double calculate() {
        return getTruePositives();
    }

    @Override
    public String getName() {
        return "TruePositives";
    }


}
