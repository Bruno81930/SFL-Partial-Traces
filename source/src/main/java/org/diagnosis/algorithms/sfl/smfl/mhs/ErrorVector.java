package org.diagnosis.algorithms.sfl.smfl.mhs;

import org.diagnosis.algorithms.entities.HitSpectrum;
import org.diagnosis.algorithms.entities.TestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ErrorVector implements Cloneable {

    private List<Boolean> errorVector;
    private HashMap<TestCase, Integer> testCaseIndexMap;
    private HashMap<Integer, TestCase> indexTestCaseMap;

    public TestCasesIterator testCases;

    public ErrorVector(HitSpectrum hitSpectrum) {
        this.errorVector = new ArrayList<>();
        this.testCaseIndexMap = new HashMap<>();
        this.indexTestCaseMap = new HashMap<>();
        int i = 0;
        for (TestCase testCase : hitSpectrum.getTests()) {
            this.errorVector.add(testCase.hasFailed());
            testCaseIndexMap.put(testCase, i);
            indexTestCaseMap.put(i, testCase);
            i++;
        }
        testCases = new TestCasesIterator();
    }

    public boolean testFailed(TestCase testCase) {
        return errorVector.get(testCaseIndexMap.get(testCase));
    }

    public int getNumberOfTests() {
        return errorVector.size();
    }

    @Override
    public ErrorVector clone() {
        try {
            ErrorVector clone = (ErrorVector) super.clone();
            clone.errorVector = new ArrayList<>(errorVector);
            clone.testCaseIndexMap = (HashMap<TestCase, Integer>) testCaseIndexMap.clone();
            clone.indexTestCaseMap = (HashMap<Integer, TestCase>) indexTestCaseMap.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public class TestCasesIterator implements Iterable<TestCase>{

        @Override
        public java.util.Iterator<TestCase> iterator() {
                return testCaseIndexMap.keySet().iterator();
        }
    }

    public void strip(TestCase testCaseToRemove) {
        int index = testCaseIndexMap.get(testCaseToRemove);
        errorVector.remove(index);
        testCaseIndexMap.remove(testCaseToRemove);
        indexTestCaseMap.remove(index);
        for (TestCase testCase : testCaseIndexMap.keySet()) {
            int i = testCaseIndexMap.get(testCase);
            if (i > index) {
                testCaseIndexMap.put(testCase, i - 1);
                indexTestCaseMap.remove(i);
                indexTestCaseMap.put(i - 1, testCase);
            }
        }
        this.testCases = new TestCasesIterator();
    }


}
