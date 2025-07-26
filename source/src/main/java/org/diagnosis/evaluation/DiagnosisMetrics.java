package org.diagnosis.evaluation;

import org.diagnosis.algorithms.sfl.ssfl.SimilarityCoefficients;
import org.diagnosis.evaluation.ground_truth.GroundTruth;
import org.diagnosis.evaluation.metrics.diagnosis.NormalizedWastedEffort;
import org.diagnosis.evaluation.metrics.diagnosis.NumberOfComponents;
import org.diagnosis.evaluation.metrics.diagnosis.WastedEffort;

public class DiagnosisMetrics extends Metrics {

    public DiagnosisMetrics(SimilarityCoefficients similarityCoefficients, GroundTruth groundTruth) {
        super();
        this.metrics.add(new NormalizedWastedEffort(similarityCoefficients, groundTruth));
        this.metrics.add(new WastedEffort(similarityCoefficients, groundTruth));
        this.metrics.add(new NumberOfComponents(similarityCoefficients, groundTruth));
    }
}
