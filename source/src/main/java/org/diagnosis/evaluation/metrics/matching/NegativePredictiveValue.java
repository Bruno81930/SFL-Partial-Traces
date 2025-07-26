package org.diagnosis.evaluation.metrics.matching;

import org.diagnosis.algorithms.entities.HitVector;

// Of all the items labeled as negative by the classifier, how many are actually negative?
public class NegativePredictiveValue extends ConfusionMatrix {

    public NegativePredictiveValue(HitVector actualExecutionTrace, HitVector predictedExecutionTrace) {
        super(actualExecutionTrace, predictedExecutionTrace);
    }

    @Override
    public double calculate() {
        if (getTrueNegatives() + getFalseNegatives() == 0) {
            return 0;
        }
        return getTrueNegatives() / (double) (getTrueNegatives() + getFalseNegatives());
    }

    @Override
    public String getName() {
        return "NegativePredictiveValue";
    }


}
