package org.diagnosis.evaluation;

public abstract class Metric {

    private double value;

    public abstract double calculate();

    public abstract String getName();

    public double setValue(double value) {
        return this.value = value;
    }

    public double getValue() {
        return value;
    }

}
