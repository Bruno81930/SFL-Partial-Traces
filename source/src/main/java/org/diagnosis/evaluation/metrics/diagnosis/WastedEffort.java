package org.diagnosis.evaluation.metrics.diagnosis;

import org.diagnosis.algorithms.entities.Component;
import org.diagnosis.algorithms.sfl.ssfl.SimilarityCoefficients;
import org.diagnosis.evaluation.Metric;
import org.diagnosis.evaluation.ground_truth.GroundTruth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * This is the complement of the examination effort. If the examination effort is 10%, the wasted effort would be 90%.
 * It represents the percentage of entities examined that were not the actual fault.
 *
 */
public class WastedEffort extends Metric {

    private final SimilarityCoefficients similarityCoefficients;
    private final GroundTruth groundTruth;

    private static final Logger logger = LoggerFactory.getLogger(WastedEffort.class);

    public WastedEffort(SimilarityCoefficients similarityCoefficients, GroundTruth groundTruth) {
        this.similarityCoefficients = similarityCoefficients;
        this.groundTruth = groundTruth;
    }

    public SimilarityCoefficients getSimilarityCoefficients() {
        return similarityCoefficients;
    }

    public GroundTruth getGroundTruth() {
        return groundTruth;
    }

    @Override
    public double calculate() {
        Map<Component, Double> formulaSimilarityCoefficients = similarityCoefficients.getCoefficients().entrySet().stream()
                .filter(entry -> entry.getValue() >= 0.0)
                .sorted(Map.Entry.<Component, Double>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        // TODO: This may not be the correct number.
        if (formulaSimilarityCoefficients.isEmpty()) {
            // If there are no components available, then the wasted effort is all the unfiltered components
            logger.info("No components available for wasted effort calculation");
            return similarityCoefficients.getCoefficients().size();
        }

        int faultyComponentsRepaired = 0;
        int totalFaultyComponents = groundTruth.getComponents().size();
        int wastedEffort = 0;

        List<Component> groundTruthComponents = groundTruth.getComponents();

        for (Map.Entry<Component, Double> entry : formulaSimilarityCoefficients.entrySet()) {
            Component component = entry.getKey();
            if (groundTruthComponents.contains(component)) {
                groundTruthComponents.remove(component);
                faultyComponentsRepaired++;
            } else {
                wastedEffort++;
            }

            if (faultyComponentsRepaired == totalFaultyComponents && groundTruthComponents.isEmpty()) {
                break;
            }
        }

        return wastedEffort;
    }

    @Override
    public String getName() {
        return "Wasted Effort";
    }
}
