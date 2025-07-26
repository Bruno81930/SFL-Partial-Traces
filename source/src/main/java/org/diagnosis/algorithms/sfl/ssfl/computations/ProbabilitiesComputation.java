package org.diagnosis.algorithms.sfl.ssfl.computations;

import org.diagnosis.algorithms.entities.Component;
import org.diagnosis.algorithms.entities.HitSpectrum;
import org.diagnosis.algorithms.entities.TestCase;
import org.diagnosis.algorithms.sfl.ssfl.CoverageMatrix;

public class ProbabilitiesComputation implements ComputationStrategy {
    @Override
    public CoverageMatrix compute(HitSpectrum spectrum, Component component) {
        float n00 = 0.0f;
        float n10 = 0.0f;
        float n01 = 0.0f;
        float n11 = 0.0f;

        for (TestCase testCase : spectrum.getTests()) {
            boolean hasFailed = testCase.hasFailed();

            if (hasFailed) {
                n11 += spectrum.getProbabilityOfHit(component, testCase);
                n01 += 1-spectrum.getProbabilityOfHit(component, testCase);
            } else {
                n10 += spectrum.getProbabilityOfHit(component, testCase);
                n00 += 1-spectrum.getProbabilityOfHit(component, testCase);
            }
        }

        return new CoverageMatrix(n00, n01, n10, n11);
    }

    @Override
    public String report() {
        return "(P)";
    }
}
