package org.diagnosis.algorithms.walker.weightModifiers;

public class SquaredModifier implements WeightModifier{
    @Override
    public double transform(float weight) {
        return Math.pow(weight, 2);
    }

    @Override
    public String report() {
        return "s";
    }
}
