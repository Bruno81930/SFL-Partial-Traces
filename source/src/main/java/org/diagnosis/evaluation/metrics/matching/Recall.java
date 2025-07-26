package org.diagnosis.evaluation.metrics.matching;

import org.diagnosis.algorithms.entities.HitVector;

// Recall answers the question: "Of all the actual positive items, how many were correctly classified as positive by the classifier?"
public class Recall extends ConfusionMatrix {

    public Recall(HitVector actualExecutionTrace, HitVector predictedExecutionTrace) {
        super(actualExecutionTrace, predictedExecutionTrace);
    }

    @Override
    public double calculate() {
        if (getTruePositives() + getFalseNegatives() == 0) {
            return 0;
        }
        return getTruePositives() / (double) (getTruePositives() + getFalseNegatives());
    }

    @Override
    public String getName() {
        return "Recall";
    }


}
