package org.diagnosis.algorithms.sfl.ssfl.formulas;

import org.diagnosis.algorithms.sfl.ssfl.Formula;

public class Tarantula extends AbstractFormula {
    @Override
    public String getName() {
        return Formula.TARANTULA.name();
    }

    @Override
    public Formula getFormula() {
        return Formula.TARANTULA;
    }

    @Override
    public double compute(final double n00, final double n01, final double n10, final double n11) {
        double nFailed = n11 + n01 == 0 ? 0 : n11 / (n11 + n01);
        double nPassed = n10 + n00 == 0 ? 0 : n10 / (n10 + n00);

        if (nFailed + nPassed == 0) {
            return 0.0;
        } else {
            return nFailed / (nFailed + nPassed);
        }
    }
}
