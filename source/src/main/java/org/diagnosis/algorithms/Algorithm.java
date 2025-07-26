package org.diagnosis.algorithms;

import org.diagnosis.algorithms.entities.HitSpectrum;
import org.diagnosis.algorithms.entities.HitVector;
import org.diagnosis.algorithms.entities.TestCase;
import org.diagnosis.algorithms.parser.ExecutionsParser;
import org.diagnosis.algorithms.sfl.smfl.SMFL;
import org.diagnosis.algorithms.sfl.ssfl.Formula;
import org.diagnosis.algorithms.sfl.ssfl.SSFL;
import org.diagnosis.algorithms.sfl.ssfl.SimilarityCoefficients;
import org.diagnosis.algorithms.sfl.ssfl.computations.BinaryComputation;
import org.diagnosis.algorithms.sfl.ssfl.computations.ComputationStrategy;

import java.util.List;

public abstract class Algorithm {
    protected HitSpectrum hitSpectrum;
    List<SimilarityCoefficients> diagnosis;
    final ComputationStrategy computationStrategy;

    public Algorithm(ComputationStrategy computationStrategy) {
        this.hitSpectrum = new HitSpectrum();
        this.computationStrategy = computationStrategy;
    }

    public Algorithm() {
        this.hitSpectrum = new HitSpectrum();
        this.computationStrategy = new BinaryComputation();
    }

    public HitVector parse(String path) {
        return new ExecutionsParser().parse(path);
    }

    public void add(String packageName, String testClass, String testMethod, String path, boolean failed) {
        TestCase testCase = new TestCase(packageName, testClass, testMethod, failed);
        HitVector hitVector = this.parse(path);
        hitSpectrum.add(testCase, hitVector);
    }

    public void execute(boolean multipleFaults) {
        if (multipleFaults) {
            SMFL SMFL = new SMFL();
            SMFL.diagnose(hitSpectrum);
        } else {
            SSFL SSFL = new SSFL();
            this.diagnosis = SSFL.diagnose(hitSpectrum, computationStrategy);
        }
    }

    public SimilarityCoefficients getSimilarityCoefficients(Formula formula) throws AlgorithmException {
        if (this.diagnosis == null)
            throw new AlgorithmException("Diagnosis not executed");
        return this.diagnosis.stream().
                filter(similarityCoefficients -> similarityCoefficients.getFormula().equals(formula))
                .findFirst()
                .orElseThrow(() -> new AlgorithmException("Similarity coefficient not found"));
    }

    public HitSpectrum getHitSpectrum() {
        return hitSpectrum;
    }
}
