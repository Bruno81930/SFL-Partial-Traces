package org.diagnosis.algorithms.sfl.ssfl.formulas;

import org.diagnosis.algorithms.sfl.ssfl.Formula;

public class RogersTanimoto extends AbstractFormula {
    @Override
    public String getName() {
        return Formula.ROGERS_TANIMOTO.name();
    }

    @Override
    public Formula getFormula() {
        return Formula.ROGERS_TANIMOTO;
    }

    @Override
    public double compute(final double n00, final double n01, final double n10, final double n11) {
        if (n11 + n00 + 2 * (n01 + n10) == 0) {
            return 0.0;
        }
        return (n11 + n00) / (n11 + n00 + 2 * (n01 + n10));
    }
}
