package org.diagnosis.algorithms.walker.weightModifiers;

public interface WeightModifier {
    double transform(float weight);
    String report();
}
