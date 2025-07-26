package org.diagnosis.algorithms.walker.weightModifiers;

public class NoModifier implements WeightModifier {
    @Override
    public double transform(float weight) {
        return weight;
    }

    @Override
    public String report() {
        return "";
    }
}
