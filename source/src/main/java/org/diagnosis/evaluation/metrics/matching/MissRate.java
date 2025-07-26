package org.diagnosis.evaluation.metrics.matching;

import org.diagnosis.algorithms.entities.HitVector;

public class MissRate extends ConfusionMatrix {

    public MissRate(HitVector actualExecutionTrace, HitVector predictedExecutionTrace) {
        super(actualExecutionTrace, predictedExecutionTrace);
    }

    @Override
    public double calculate() {
        if (getFalseNegatives() + getTruePositives() == 0) {
            return 0;
        }
        return getFalseNegatives() / (double) (getFalseNegatives() + getTruePositives());
    }

    @Override
    public String getName() {
        return "MissRate";
    }


}
