package org.diagnosis.algorithms.sfl.ssfl.computations;

import org.diagnosis.algorithms.entities.Component;
import org.diagnosis.algorithms.entities.HitSpectrum;
import org.diagnosis.algorithms.sfl.ssfl.CoverageMatrix;

public interface ComputationStrategy {
    CoverageMatrix compute(HitSpectrum spectrum, Component component);
    String report();
}
