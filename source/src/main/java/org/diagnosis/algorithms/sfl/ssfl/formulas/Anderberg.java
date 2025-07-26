package org.diagnosis.algorithms.sfl.ssfl.formulas;

import org.diagnosis.algorithms.sfl.ssfl.Formula;

public class Anderberg extends AbstractFormula {
    @Override
    public String getName() {
        return Formula.ANDERBERG.name();
    }

    @Override
    public Formula getFormula() {
        return Formula.ANDERBERG;
    }

    @Override
    public double compute(final double n00, final double n01, final double n10, final double n11) {
        if (2 * n11 + n01 + n10 == 0) {
            return 0.0;
        }
        return n11 / (n11 + 2 * (n01 + n10));
    }
}
