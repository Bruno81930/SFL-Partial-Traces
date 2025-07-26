package org.diagnosis.algorithms.sfl.ssfl.formulas;

import org.diagnosis.algorithms.sfl.ssfl.Formula;

public class Naish1 extends AbstractFormula {
    @Override
    public String getName() {
        return Formula.NAISH1.name();
    }

    @Override
    public Formula getFormula() {
        return Formula.NAISH1;
    }

    @Override
    public double compute(final double n00, final double n01, final double n10, final double n11) {
        if (n01 > 0.0) {
            return -1.0;
        }
        return n00;
    }
}
