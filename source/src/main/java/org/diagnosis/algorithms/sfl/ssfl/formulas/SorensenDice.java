package org.diagnosis.algorithms.sfl.ssfl.formulas;

import org.diagnosis.algorithms.sfl.ssfl.Formula;

public class SorensenDice extends AbstractFormula {
    @Override
    public String getName() {
        return Formula.SORENSEN_DICE.name();
    }

    @Override
    public Formula getFormula() {
        return Formula.SORENSEN_DICE;
    }

    @Override
    public double compute(final double n00, final double n01, final double n10, final double n11) {
        if (2 * n11 + n01 + n10 == 0) {
            return 0.0;
        }
        return (2 * n11) / (2 * n11 + n01 + n10);
    }
}
