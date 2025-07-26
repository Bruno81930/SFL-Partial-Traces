package org.diagnosis.algorithms.filter;

import org.diagnosis.algorithms.entities.Component;
import org.diagnosis.algorithms.entities.HitVector;
import org.diagnosis.algorithms.entities.TestCase;
import org.diagnosis.evaluation.ground_truth.GroundTruth;

import java.util.*;

public class RandomWithFaultsFilterStrategy extends FilterStrategy {
    final float percentageOfComponents;
    final GroundTruth groundTruth;
    Random random;
    Long seed = null;

    public RandomWithFaultsFilterStrategy(float percentageOfComponents, GroundTruth groundTruth) {
        this.percentageOfComponents = percentageOfComponents;
        this.groundTruth = groundTruth;
        this.random = new Random();
    }

    public RandomWithFaultsFilterStrategy(float percentageOfComponents, long seed, GroundTruth groundTruth) {
        this.percentageOfComponents = percentageOfComponents;
        this.groundTruth = groundTruth;
        this.random = new Random(seed);
        this.seed = seed;
    }

    public void setSeed(long seed) {
        this.random = new Random(seed);
    }

    @Override
    public void filterComponents(HashMap<TestCase, HitVector> hitSpectrum, List<Component> allComponents) {
        int numberOfPercentageFilteredComponents = (int) Math.ceil(allComponents.size() * (percentageOfComponents / 100.0));
        int numberOfFilteredComponents = numberOfPercentageFilteredComponents - groundTruth.getComponents().size();

        if (numberOfPercentageFilteredComponents > 0) {
            allComponents.removeAll(groundTruth.getComponents());
        }

        for (int i = 0; i < numberOfFilteredComponents; i++) {
            allComponents.remove(this.random.nextInt(allComponents.size()));
        }



        Set<Component> components = new HashSet<>(allComponents);
        for (TestCase testCase : hitSpectrum.keySet()) {
            HitVector hitVector = hitSpectrum.get(testCase);
            hitVector.filterComponentsGivenWhichToKeep(components);
        }
    }

    @Override
    public String getName() {
        return String.format("Random With Faults", this.percentageOfComponents);
    }
}
