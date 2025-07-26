package org.diagnosis.algorithms.sfl.ssfl.formulas;

import org.diagnosis.algorithms.sfl.ssfl.Formula;

public class DStar extends AbstractFormula {
    @Override
    public String getName() {
        return Formula.DSTAR.name();
    }

    @Override
    public Formula getFormula() {
        return Formula.DSTAR;
    }

    @Override
    public double compute(final double n00, final double n01, final double n10, final double n11) {
        if ((n10 + n01 == 0) || (n11 == 0)) {
            return 0.0;
        }
        return Math.pow(n11, 2.0) / (n10 + n01);
    }
}
