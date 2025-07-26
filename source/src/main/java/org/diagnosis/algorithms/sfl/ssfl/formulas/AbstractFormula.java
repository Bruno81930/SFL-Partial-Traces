package org.diagnosis.algorithms.sfl.ssfl.formulas;

import org.diagnosis.algorithms.sfl.ssfl.Formula;

public abstract class AbstractFormula {

    public abstract String getName();

    public abstract Formula getFormula();

    public abstract double compute(final double n00, final double n01, final double n10,
                                   final double n11);
}
