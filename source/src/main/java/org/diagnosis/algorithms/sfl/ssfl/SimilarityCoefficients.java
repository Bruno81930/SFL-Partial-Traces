package org.diagnosis.algorithms.sfl.ssfl;

import org.diagnosis.algorithms.entities.Component;
import org.diagnosis.algorithms.entities.HitSpectrum;
import org.diagnosis.algorithms.entities.TestCase;
import org.diagnosis.algorithms.sfl.ssfl.computations.BinaryComputation;
import org.diagnosis.algorithms.sfl.ssfl.computations.ComputationStrategy;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SimilarityCoefficients {

    private final Formula formula;
    private final Map<Component, Double> coefficients;
    private final Map<Component, Float> coefficientN00;
    private final Map<Component, Float> coefficientN10;
    private final Map<Component, Float> coefficientN01;
    private final Map<Component, Float> coefficientN11;
    private final ComputationStrategy computation;

    public SimilarityCoefficients(Formula formula, ComputationStrategy computation) {
        coefficients = new LinkedHashMap<>();
        this.formula = formula;
        this.coefficientN00 = new HashMap<>();
        this.coefficientN10 = new HashMap<>();
        this.coefficientN01 = new HashMap<>();
        this.coefficientN11 = new HashMap<>();
        this.computation = computation;
    }

    public void addCoefficient(Component component, double coefficient) {
        this.coefficients.put(component, coefficient);
    }

    public void compute(HitSpectrum spectrum) {
        List<Component> components = spectrum.getComponents(false);
        for (Component component : components) {
            CoverageMatrix coverageMatrix = computation.compute(spectrum, component);

            this.addN00(component, coverageMatrix.getN00());
            this.addN01(component, coverageMatrix.getN01());
            this.addN10(component, coverageMatrix.getN10());
            this.addN11(component, coverageMatrix.getN11());

            this.addCoefficient(component, formula.getFormula().compute(
                    coverageMatrix.getN00(),
                    coverageMatrix.getN01(),
                    coverageMatrix.getN10(),
                    coverageMatrix.getN11()));
        }

        for (Component filteredComponent : spectrum.getFilteredComponents().stream().filter(component -> !components.contains(component)).collect(Collectors.toList())) {
            int filteredCoefficient = 0;
            this.addCoefficient(filteredComponent, filteredCoefficient);
            this.addN00(filteredComponent, filteredCoefficient);
            this.addN01(filteredComponent, filteredCoefficient);
            this.addN10(filteredComponent, filteredCoefficient);
            this.addN11(filteredComponent, filteredCoefficient);
        }
    }

    public void addN00(Component component, float n00) {
        this.coefficientN00.put(component, n00);
    }

    public void addN10(Component component, float n10) {
        this.coefficientN10.put(component, n10);
    }

    public void addN01(Component component, float n01) {
        this.coefficientN01.put(component, n01);
    }

    public void addN11(Component component, float n11) {
        this.coefficientN11.put(component, n11);
    }

    public float N00(Component component) {
        return this.coefficientN00.get(component);
    }

    public float N10(Component component) {
        return this.coefficientN10.get(component);
    }

    public float N01(Component component) {
        return this.coefficientN01.get(component);
    }

    public float N11(Component component) {
        return this.coefficientN11.get(component);
    }


    public String getFormulaName() {
        return formula.name();
    }

    public Formula getFormula() {
        return formula;
    }

    public Map<Component, Double> getCoefficients() {
        return coefficients;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimilarityCoefficients)) return false;

        SimilarityCoefficients that = (SimilarityCoefficients) o;

        if (formula != that.formula) return false;
        return coefficients.equals(that.coefficients);
    }
}
