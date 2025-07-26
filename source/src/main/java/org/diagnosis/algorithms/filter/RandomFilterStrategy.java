package org.diagnosis.algorithms.filter;

import org.diagnosis.algorithms.entities.Component;
import org.diagnosis.algorithms.entities.HitVector;
import org.diagnosis.algorithms.entities.TestCase;

import java.util.*;

public class RandomFilterStrategy extends FilterStrategy{

    final float percentageOfComponents;
    Random random;
    Long seed = null;

    public RandomFilterStrategy(float percentageOfComponents) {
        this.percentageOfComponents = percentageOfComponents;
        this.random = new Random();
    }

    public RandomFilterStrategy(float percentageOfComponents, long seed) {
        this.percentageOfComponents = percentageOfComponents;
        this.seed = seed;
        this.random = new Random(seed);
    }

    public void reset() {
        if (this.seed != null) {
            this.random = new Random(this.seed);
        }
    }

    public void setSeed(long seed) {
        this.seed = seed;
        this.random = new Random(seed);
    }

    @Override
    public void filterComponents(HashMap<TestCase, HitVector> hitSpectrum, List<Component> allComponents) {
        reset();
        int numberOfFilteredComponents = (int) Math.ceil(allComponents.size() * (percentageOfComponents / 100.0));
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
        return String.format("Random", this.percentageOfComponents);
    }
}
