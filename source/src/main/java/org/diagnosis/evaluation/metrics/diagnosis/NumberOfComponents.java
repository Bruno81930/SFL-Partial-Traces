package org.diagnosis.evaluation.metrics.diagnosis;

import org.diagnosis.algorithms.sfl.ssfl.SimilarityCoefficients;
import org.diagnosis.evaluation.Metric;
import org.diagnosis.evaluation.ground_truth.GroundTruth;


/**
 * This is the complement of the examination effort. If the examination effort is 10%, the wasted effort would be 90%.
 * It represents the percentage of entities examined that were not the actual fault.
 *
 */
public class NumberOfComponents extends Metric {

    private final SimilarityCoefficients similarityCoefficients;

    public NumberOfComponents(SimilarityCoefficients similarityCoefficients, GroundTruth groundTruth) {
        this.similarityCoefficients = similarityCoefficients;
    }

    public SimilarityCoefficients getSimilarityCoefficients() {
        return similarityCoefficients;
    }

    @Override
    public double calculate() {
        return this.similarityCoefficients.getCoefficients().size();
    }

    @Override
    public String getName() {
        return "Number of Components";
    }
}
