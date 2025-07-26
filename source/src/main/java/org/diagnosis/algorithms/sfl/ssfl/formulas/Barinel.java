package org.diagnosis.algorithms.sfl.ssfl.formulas;

import org.diagnosis.algorithms.sfl.ssfl.Formula;

public class Barinel extends AbstractFormula {
    @Override
    public String getName() {
        return Formula.BARINEL.name();
    }

    @Override
    public Formula getFormula() {
        return Formula.BARINEL;
    }

    @Override
    public double compute(final double n00, final double n01, final double n10, final double n11) {
        if (n10 + n11 == 0.0) {
            return 0.0;
        }
        double h = n10 / (n10 + n11);
        return Math.pow(h, n10) * Math.pow(1 - h, 11);
    }
}
