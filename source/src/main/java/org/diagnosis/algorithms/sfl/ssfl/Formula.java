package org.diagnosis.algorithms.sfl.ssfl;

import org.diagnosis.algorithms.sfl.ssfl.formulas.*;

public enum Formula {

    ANDERBERG(new Anderberg()),
    BARINEL(new Barinel()),
    DSTAR(new DStar()),
    IDEAL(new Ideal()),
    JACCARD(new Jaccard()),
    KULCZYNSKI2(new Kulczynski2()),
    NAISH1(new Naish1()),
    OCHIAI(new Ochiai()),
    OCHIAI2(new Ochiai2()),
    OPT(new Opt()),
    ROGERS_TANIMOTO(new RogersTanimoto()),
    RUSSEL_RAO(new RusselRao()),
    SBI(new SBI()),
    SIMPLE_MATCHING(new SimpleMatching()),
    SORENSEN_DICE(new SorensenDice()),
    TARANTULA(new Tarantula());

    private final AbstractFormula abstractFormula;

    Formula(final AbstractFormula abstractFormula) {
        this.abstractFormula = abstractFormula;
    }

    public AbstractFormula getFormula() {
        return this.abstractFormula;
    }
}
