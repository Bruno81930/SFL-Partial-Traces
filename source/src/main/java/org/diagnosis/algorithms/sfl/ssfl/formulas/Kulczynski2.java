package org.diagnosis.algorithms.sfl.ssfl.formulas;

import org.diagnosis.algorithms.sfl.ssfl.Formula;

public class Kulczynski2 extends AbstractFormula {
    @Override
    public String getName() {
        return Formula.KULCZYNSKI2.name();
    }

    @Override
    public Formula getFormula() {
        return Formula.KULCZYNSKI2;
    }

    @Override
    public double compute(final double n00, final double n01, final double n10, final double n11) {
        if ((n11 + n01 == 0) || (n11 + n10 == 0)) {
            return 0.0;
        }
        return 0.5 * ((n11 / (n11 + n01)) + (n11 / (n11 + n10)));
    }
}
