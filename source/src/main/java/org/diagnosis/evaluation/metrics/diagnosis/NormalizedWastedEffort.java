package org.diagnosis.evaluation.metrics.diagnosis;

import org.diagnosis.algorithms.sfl.ssfl.SimilarityCoefficients;
import org.diagnosis.evaluation.ground_truth.GroundTruth;


/**
 * This is the complement of the examination effort. If the examination effort is 10%, the wasted effort would be 90%.
 * It represents the percentage of entities examined that were not the actual fault.
 *
 */
public class NormalizedWastedEffort extends WastedEffort {
    public NormalizedWastedEffort(SimilarityCoefficients similarityCoefficients, GroundTruth groundTruth) {
        super(similarityCoefficients, groundTruth);
    }

    @Override
    public double calculate() {
        double wastedEffort = super.calculate();
        double totalComponents = super.getSimilarityCoefficients().getCoefficients().size();
        return (wastedEffort / totalComponents);
    }


    @Override
    public String getName() {
        return "Normalized Wasted Effort";
    }
}
