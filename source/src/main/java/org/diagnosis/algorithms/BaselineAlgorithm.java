package org.diagnosis.algorithms;

import org.diagnosis.algorithms.entities.HitSpectrum;
import org.diagnosis.algorithms.entities.HitVector;
import org.diagnosis.algorithms.entities.TestCase;
import org.diagnosis.algorithms.filter.FilterStrategy;
import org.diagnosis.algorithms.parser.ExecutionsParser;

public class BaselineAlgorithm extends Algorithm {

    final FilterStrategy filterStrategy;

    private final HitSpectrum fullTrace;

    public BaselineAlgorithm(FilterStrategy filterStrategy) {
        this.filterStrategy = filterStrategy;
        this.fullTrace = new HitSpectrum();
    }

    protected void filterTraces() {
        hitSpectrum.filterComponents(this.filterStrategy);
    }

    public void execute(boolean multipleFaults) {
        this.execute(multipleFaults, null);
    }

    public void execute(boolean multipleFaults, TestCase testCase) {
        filterTraces();
        if (testCase != null) {
            setTestCase(testCase);
        }
        super.execute(multipleFaults);
    }

    protected void setTestCase(TestCase testCase) {
        HitVector hitVector = hitSpectrum.get(testCase);
        hitSpectrum.getHitSpectrum().clear();
        hitSpectrum.add(testCase, hitVector);
    }

    @Override
    public void add(String packageName, String testClass, String testMethod, String path, boolean failed) {
        TestCase testCase = new TestCase(packageName, testClass, testMethod, failed);
        HitVector hitVector = new ExecutionsParser().parse(path);
        hitSpectrum.add(testCase, hitVector);
        fullTrace.add(testCase, hitVector.clone());
    }

    public HitSpectrum getFullTrace() {
        return fullTrace;
    }

}
