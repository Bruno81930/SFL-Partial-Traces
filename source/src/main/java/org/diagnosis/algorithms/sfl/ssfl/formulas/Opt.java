package org.diagnosis.algorithms.sfl.ssfl.formulas;

import org.diagnosis.algorithms.sfl.ssfl.Formula;

public class Opt extends AbstractFormula {
    @Override
    public String getName() {
        return Formula.OPT.name();
    }

    @Override
    public Formula getFormula() {
        return Formula.OPT;
    }

    @Override
    public double compute(final double n00, final double n01, final double n10, final double n11) {
        return n11 - (n10 / (n10 + n00 + 1.0));
    }
}
