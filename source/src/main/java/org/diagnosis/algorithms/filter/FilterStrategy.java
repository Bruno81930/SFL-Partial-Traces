package org.diagnosis.algorithms.filter;

import org.diagnosis.algorithms.entities.Component;
import org.diagnosis.algorithms.entities.HitVector;
import org.diagnosis.algorithms.entities.TestCase;

import java.util.HashMap;
import java.util.List;

public abstract class FilterStrategy {

    public abstract void filterComponents(HashMap<TestCase, HitVector> hitSpectrum, List<Component> allComponents);
    public abstract String getName();
    public abstract void setSeed(long seed);

}
