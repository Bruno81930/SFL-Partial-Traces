package org.diagnosis.evaluation.metrics.matching;

import org.diagnosis.algorithms.entities.HitVector;

// The F1-score is the harmonic mean of precision and recall, providing a balance between the two. An F1-score reaches its best value at 1 (perfect precision and recall) and the worst at 0.
public class F1Score extends ConfusionMatrix {

    public F1Score(HitVector actualExecutionTrace, HitVector predictedExecutionTrace) {
        super(actualExecutionTrace, predictedExecutionTrace);
    }

    @Override
    public double calculate() {
        double precision;
        if (getTruePositives() + getFalsePositives() == 0) {
            precision = 0;
        } else {
            precision = getTruePositives() / (double) (getTruePositives() + getFalsePositives());
        }

        double recall;
        if (getTruePositives() + getFalseNegatives() == 0) {
            recall = 0;
        } else {
            recall = getTruePositives() / (double) (getTruePositives() + getFalseNegatives());
        }

        if (precision + recall == 0) {
            return 0;
        }

        return 2 * precision * recall / (precision + recall);
    }

    @Override
    public String getName() {
        return "F1Score";
    }


}
