package org.diagnosis.evaluation.metrics.matching;

import org.diagnosis.algorithms.entities.Component;
import org.diagnosis.algorithms.entities.Hit;
import org.diagnosis.algorithms.entities.HitVector;
import org.diagnosis.evaluation.Metric;
import org.diagnosis.evaluation.ground_truth.GroundTruth;

import java.util.List;
import java.util.stream.Collectors;

// The F1-score is the harmonic mean of precision and recall, providing a balance between the two. An F1-score reaches its best value at 1 (perfect precision and recall) and the worst at 0.
public class GroundTruthHits extends Metric {

    private final GroundTruth groundTruth;
    private final HitVector hitVector;
    private final String executionName;

    public GroundTruthHits(GroundTruth groundTruth, HitVector hitVector, String executionName) {
        this.groundTruth = groundTruth;
        this.hitVector = hitVector;
        this.executionName = executionName;
    }

    @Override
    public double calculate() {
        float numberOfHits = 0.0f;
        for (Component component : groundTruth.getComponents()) {
            List<Hit> groundTruthHits = hitVector.stream().filter(hit -> hit.getComponent().equals(component) && hit.isHit()).collect(Collectors.toList());
            if (groundTruthHits.size() > 0) {
                numberOfHits += 1.0f;
            }
        }
        return numberOfHits;
    }

    @Override
    public String getName() {
        return String.format("GroundTruthHits%s", this.executionName);
    }


}
