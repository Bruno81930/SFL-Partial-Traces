package org.diagnosis.algorithms.sfl.smfl.mhs;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.diagnosis.algorithms.entities.Component;
import org.diagnosis.algorithms.entities.HitSpectrum;
import org.diagnosis.algorithms.entities.TestCase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class HitMatrix implements Cloneable{

    private Array2DRowRealMatrix hitMatrix;
    private HashMap<Component, Integer> componentIndexMap;
    private HashMap<Integer, Component> indexComponentMap;
    private HashMap<TestCase, Integer> testIndexMap;
    private HashMap<Integer, TestCase> indexTestMap;

    private boolean isHitMatrixEmpty;

    public ComponentsIterable components;
    public TestsIterable tests;


    public HitMatrix(HitSpectrum hitSpectrum) {
        if (hitSpectrum.getComponents(false).size() != 0) {
            this.isHitMatrixEmpty = false;
        } else {
            this.isHitMatrixEmpty = true;
            return;
        }
        hitMatrix = new Array2DRowRealMatrix(hitSpectrum.size(), hitSpectrum.getComponents(false).size());
        componentIndexMap = new HashMap<>();
        indexComponentMap = new HashMap<>();
        testIndexMap = new HashMap<>();
        indexTestMap = new HashMap<>();
        int testCaseId = 0;
        for (TestCase testCase : hitSpectrum.getTests()) {
            int componentId = 0;
            testIndexMap.put(testCase, testCaseId);
            indexTestMap.put(testCaseId, testCase);
            for (Component component : hitSpectrum.getComponents(false)) {
                if (testCaseId == 0) {
                    componentIndexMap.put(component, componentId);
                    indexComponentMap.put(componentId, component);
                }
                if (hitSpectrum.get(testCase).isHit(component)) {
                    hitMatrix.setEntry(testCaseId, componentId, 1);
                } else {
                    hitMatrix.setEntry(testCaseId, componentId,0);
                }
                componentId++;
            }
            testCaseId++;
        }
        components = new ComponentsIterable();
        tests = new TestsIterable();


    }

    public Set<Conflict> getConflicts(ErrorVector errorVector) {
        Set<Conflict> conflicts = new HashSet<>();

        for (int testRow = 0; testRow < hitMatrix.getRowDimension(); testRow++) {
            if (errorVector.testFailed(indexTestMap.get(testRow))) {
                Conflict conflict = new Conflict();
                for (int componentColumn = 0; componentColumn < hitMatrix.getColumnDimension(); componentColumn++) {
                    if (hitMatrix.getEntry(testRow, componentColumn) == 1) {
                        conflict.add(indexComponentMap.get(componentColumn));
                    }
                }
                if (!conflict.isEmpty()) {
                    conflicts.add(conflict);
                }
            }
        }

        return conflicts;
    }

    public boolean isEmpty() {
        return this.isHitMatrixEmpty;
    }

    public void strip(Component componentToRemove) {
        if (this.isHitMatrixEmpty || this.hitMatrix.getColumnDimension() == 1) {
            this.isHitMatrixEmpty = true;
            return;
        }

        Array2DRowRealMatrix newHitMatrix = new Array2DRowRealMatrix(hitMatrix.getRowDimension(), hitMatrix.getColumnDimension() - 1);
        HashMap<Component, Integer> newComponentIndexMap = new HashMap<>();
        HashMap<Integer, Component> newIndexComponentMap = new HashMap<>();
        int componentIdToRemove = componentIndexMap.get(componentToRemove);

        for (int testCaseId = 0; testCaseId < hitMatrix.getRowDimension(); testCaseId++) {
            for (int componentId = 0, newComponentId = 0; componentId < hitMatrix.getColumnDimension(); componentId++) {
                if (componentId != componentIdToRemove) {
                    if (testCaseId == 0) {
                        newComponentIndexMap.put(indexComponentMap.get(componentId), newComponentId);
                        newIndexComponentMap.put(newComponentId, indexComponentMap.get(componentId));
                    }
                    newHitMatrix.setEntry(testCaseId, newComponentId++, hitMatrix.getEntry(testCaseId, componentId));

                }
            }
        }
        this.hitMatrix = newHitMatrix;
        this.componentIndexMap = newComponentIndexMap;
        this.indexComponentMap = newIndexComponentMap;
    }

    public void strip(Component componentToRemove, ErrorVector errorVector) {
        for (TestCase testCase : errorVector.testCases) {
            if (errorVector.testFailed(testCase)) {
                if (hitMatrix.getEntry(testIndexMap.get(testCase), componentIndexMap.get(componentToRemove)) == 1) {
                    errorVector.strip(testCase);
                }
            }
        }
        Array2DRowRealMatrix newHitMatrix = new Array2DRowRealMatrix(errorVector.getNumberOfTests(), hitMatrix.getColumnDimension() - 1);
        HashMap<Component, Integer> newComponentIndexMap = new HashMap<>();
        HashMap<Integer, Component> newIndexComponentMap = new HashMap<>();
        int componentIdToRemove = componentIndexMap.get(componentToRemove);
        HashMap<TestCase, Integer> newTestIndexMap = new HashMap<>();
        HashMap<Integer, TestCase> newIndexTestMap = new HashMap<>();

        int testCaseId = 0;
        for (TestCase testCase : errorVector.testCases) {
            newTestIndexMap.put(testCase, testCaseId);
            newIndexTestMap.put(testCaseId, testCase);
            for (int componentId = 0, newComponentId = 0; componentId < hitMatrix.getColumnDimension(); componentId++) {
                if (componentId != componentIdToRemove) {
                    if (testCaseId == 0) {
                        newComponentIndexMap.put(indexComponentMap.get(componentId), newComponentId);
                        newIndexComponentMap.put(newComponentId, indexComponentMap.get(componentId));
                    }
                    newHitMatrix.setEntry(testCaseId, newComponentId++, hitMatrix.getEntry(testCaseId, componentId));
                }
            }
            testCaseId++;
        }
        this.hitMatrix = newHitMatrix;
        this.componentIndexMap = newComponentIndexMap;
        this.indexComponentMap = newIndexComponentMap;
        this.testIndexMap = newTestIndexMap;
        this.indexTestMap = newIndexTestMap;
        components = new ComponentsIterable();
        tests = new TestsIterable();
    }

    @Override
    public HitMatrix clone() {
        try {
            HitMatrix clone = (HitMatrix) super.clone();
            clone.hitMatrix = (Array2DRowRealMatrix) hitMatrix.copy();
            clone.componentIndexMap = (HashMap<Component, Integer>) componentIndexMap.clone();
            clone.indexComponentMap = (HashMap<Integer, Component>) indexComponentMap.clone();
            clone.testIndexMap = (HashMap<TestCase, Integer>) testIndexMap.clone();
            clone.indexTestMap = (HashMap<Integer, TestCase>) indexTestMap.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public class ComponentsIterable implements Iterable<Component> {

        @Override
        public java.util.Iterator<Component> iterator() {
            return componentIndexMap.keySet().iterator();
        }
    }

    public class TestsIterable implements Iterable<TestCase> {

        @Override
        public java.util.Iterator<TestCase> iterator() {
            return testIndexMap.keySet().iterator();
        }
    }

    public int getNumberOfComponents() {
        return hitMatrix.getColumnDimension();
    }
}
