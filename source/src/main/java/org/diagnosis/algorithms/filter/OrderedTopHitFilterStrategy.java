package org.diagnosis.algorithms.filter;

import org.diagnosis.algorithms.entities.Component;
import org.diagnosis.algorithms.entities.HitVector;
import org.diagnosis.algorithms.entities.TestCase;

import java.util.HashMap;
import java.util.List;

public class OrderedTopHitFilterStrategy extends FilterStrategy{

    final float percentageOfComponents;

    public OrderedTopHitFilterStrategy(float percentageOfComponents) {
        this.percentageOfComponents = percentageOfComponents;
    }

    @Override
    public void filterComponents(HashMap<TestCase, HitVector> hitSpectrum, List<Component> allComponents) {
       // TODO: Implement this method
    }

    @Override
    public String getName() {
        return "Ordered Top Hit Selection Heuristic";
    }

    public void setSeed(long seed) {
    }
}
