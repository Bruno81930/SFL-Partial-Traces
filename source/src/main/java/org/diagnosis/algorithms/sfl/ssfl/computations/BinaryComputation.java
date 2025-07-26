package org.diagnosis.algorithms.sfl.ssfl.computations;

import org.diagnosis.algorithms.entities.Component;
import org.diagnosis.algorithms.entities.HitSpectrum;
import org.diagnosis.algorithms.entities.TestCase;
import org.diagnosis.algorithms.sfl.ssfl.CoverageMatrix;

public class BinaryComputation implements ComputationStrategy {

    @Override
    public CoverageMatrix compute(HitSpectrum spectrum, Component component) {
        int n00 = 0;
        int n10 = 0;
        int n01 = 0;
        int n11 = 0;

        for (TestCase testCase : spectrum.getTests()) {
            boolean hasFailed = testCase.hasFailed();

            // All of this section changes based on the algorithm
            if (spectrum.isHit(component, testCase)) {
                if (hasFailed) {
                    n11++;
                } else {
                    n10++;
                }
            } else {
                if (hasFailed) {
                    n01++;
                } else {
                    n00++;
                }
            }
        }

        return new CoverageMatrix(n00, n01, n10, n11);
    }

    @Override
    public String report() {
        return "(B)";
    }
}
