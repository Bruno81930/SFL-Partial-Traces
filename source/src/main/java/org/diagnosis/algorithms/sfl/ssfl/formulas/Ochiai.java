package org.diagnosis.algorithms.sfl.ssfl.formulas;

import org.diagnosis.algorithms.sfl.ssfl.Formula;

public class Ochiai extends AbstractFormula {
    @Override
    public String getName() {
        return Formula.OCHIAI.name();
    }

    @Override
    public Formula getFormula() {
        return Formula.OCHIAI;
    }

    @Override
    public double compute(final double n00, final double n01, final double n10, final double n11) {
        if ((n11 + n10 == 0) || (n11 + n01 == 0)) {
            return 0.0;
        }
        return n11 / Math.sqrt((n11 + n01) * (n11 + n10));
    }
}
