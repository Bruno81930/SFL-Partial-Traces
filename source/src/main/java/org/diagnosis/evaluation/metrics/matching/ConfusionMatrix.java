package org.diagnosis.evaluation.metrics.matching;

import org.diagnosis.algorithms.entities.Hit;
import org.diagnosis.algorithms.entities.HitVector;
import org.diagnosis.evaluation.Metric;

import java.util.List;
import java.util.stream.Collectors;

public abstract class ConfusionMatrix extends Metric {

    private int truePositives;
    private int falsePositives;
    private int trueNegatives;
    private int falseNegatives;

    public ConfusionMatrix(HitVector actualExecutionTrace, HitVector predictedExecutionTrace) {
        this.truePositives = 0;
        this.falsePositives = 0;
        this.trueNegatives = 0;
        this.falseNegatives = 0;

        for (Hit actualHit : actualExecutionTrace) {
            Hit predictedHit = null;
            if (!predictedExecutionTrace.contains(actualHit)) {
                predictedHit = new Hit(actualHit.getComponent(), -1);
            } else {
                predictedHit = predictedExecutionTrace.getHit(actualHit.getComponent());
            }

            if (actualHit.isHit()) {
                this.truePositives += predictedHit.getProbability();
                this.falseNegatives += 1 - predictedHit.getProbability();
            } else {
                this.falsePositives += predictedHit.getProbability();
                this.trueNegatives += 1 - predictedHit.getProbability();
            }
        }
    }

    @Override
    public String getName() {
        return "Matching Confusion Matrix";
    }

    public int getTruePositives() {
        return truePositives;
    }

    public int getFalsePositives() {
        return falsePositives;
    }

    public int getTrueNegatives() {
        return trueNegatives;
    }

    public int getFalseNegatives() {
        return falseNegatives;
    }
}
