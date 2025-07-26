package org.diagnosis.algorithms.sfl.ssfl;

import org.diagnosis.algorithms.entities.HitSpectrum;
import org.diagnosis.algorithms.sfl.ssfl.computations.ComputationStrategy;
import org.diagnosis.algorithms.sfl.ssfl.formulas.AbstractFormula;

import java.util.ArrayList;
import java.util.List;

public class SSFL {
    private final List<AbstractFormula> abstractFormulas = new ArrayList<>();

    public SSFL() {
        for (Formula formula : Formula.values()) {
            this.abstractFormulas.add(formula.getFormula());
        }
    }

    public List<SimilarityCoefficients> diagnose(HitSpectrum spectrum, ComputationStrategy computationStrategy) {
        List<SimilarityCoefficients> coefficients = new ArrayList<>();
        for (AbstractFormula abstractFormula : this.abstractFormulas) {
            SimilarityCoefficients coefficient = new SimilarityCoefficients(abstractFormula.getFormula(), computationStrategy);
            coefficients.add(coefficient);
            coefficient.compute(spectrum);
        }
        return coefficients;
    }
}
