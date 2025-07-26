package org.diagnosis.evaluation.metrics.matching;

import org.diagnosis.algorithms.entities.HitVector;

// Ratio of the number of statements/classes correctly identified in the parsed logs to
// the total number of statements/classes in the full execution trace.
public class Accuracy extends ConfusionMatrix {

    public Accuracy(HitVector actualExecutionTrace, HitVector predictedExecutionTrace) {
        super(actualExecutionTrace, predictedExecutionTrace);
    }

    @Override
    public double calculate() {
        if (getTruePositives() + getFalsePositives() + getTrueNegatives() + getFalseNegatives() == 0)
            return 0;
        return (float) (getTruePositives() + getTrueNegatives()) / (getTruePositives() + getFalsePositives() + getTrueNegatives() + getFalseNegatives());

    }

    @Override
    public String getName() {
        return "Accuracy";
    }
}
