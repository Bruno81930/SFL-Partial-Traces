package org.diagnosis.evaluation.metrics.matching;

import org.diagnosis.algorithms.entities.HitVector;

// Precision answers the question: "Of all the items labeled as positive by the classifier, how many are actually positive?"
public class Precision extends ConfusionMatrix {

    public Precision(HitVector actualExecutionTrace, HitVector predictedExecutionTrace) {
        super(actualExecutionTrace, predictedExecutionTrace);
    }

    @Override
    public double calculate() {
        if (getTruePositives() + getFalsePositives() == 0) {
            return 1;
        }
        return getTruePositives() / (double) (getTruePositives() + getFalsePositives());
    }

    @Override
    public String getName() {
        return "Precision";
    }


}
