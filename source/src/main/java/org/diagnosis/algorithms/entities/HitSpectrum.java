package org.diagnosis.algorithms.entities;

import org.apache.commons.lang3.tuple.Triple;
import org.diagnosis.algorithms.filter.FilterStrategy;
import org.diagnosis.algorithms.reconstruction.Reconstruction;
import org.diagnosis.algorithms.reconstruction.ReconstructionApproach;
import org.diagnosis.algorithms.reconstruction.ReconstructionException;
import org.diagnosis.algorithms.reconstruction.graph.ExecutionGraph;
import org.diagnosis.evaluation.ground_truth.GroundTruth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class HitSpectrum {
    Logger logger = LoggerFactory.getLogger(HitSpectrum.class);

    private final HashMap<TestCase, HitVector> hitSpectrum;
    private HashMap<TestCase, ExecutionGraph> executionGraphHashMap;

    public HitSpectrum() {
        this.hitSpectrum = new HashMap<>();
    }

    public List<Component> getComponents(boolean immutable) {
        Set<Component> components = new HashSet<>();
        for (TestCase testCase : hitSpectrum.keySet()) {
            HitVector hitVector = hitSpectrum.get(testCase);
            components.addAll(hitVector.getComponents());
        }
        if (immutable) {
            return Collections.unmodifiableList(new ArrayList<>(components));
        }
        return new ArrayList<>(components);
    }

    public List<Component> getFilteredComponents() {
        Set<Component> components = new HashSet<>();
        for (TestCase testCase : hitSpectrum.keySet()) {
            HitVector hitVector = hitSpectrum.get(testCase);
            components.addAll(hitVector.getFilteredComponents());
        }
        return new ArrayList<>(components);
    }

    public List<Component> getTraces() {
        Set<Component> traces = new HashSet<>();
        for (TestCase testCase : hitSpectrum.keySet()) {
            HitVector hitVector = hitSpectrum.get(testCase);
            traces.addAll(hitVector.getTraces());
        }
        return Collections.unmodifiableList(new ArrayList<>(traces));
    }

    public Set<TestCase> getTests() {
        return hitSpectrum.keySet();
    }

    public void add(TestCase testCase, HitVector hitVector) {
        hitSpectrum.put(testCase, hitVector);
    }

    public HitVector get(TestCase testCase) {
        return hitSpectrum.get(testCase);
    }

    public int size() {
        return hitSpectrum.size();
    }

    public boolean isEmpty() {
        return hitSpectrum.isEmpty();
    }

    public boolean containsKey(TestCase testCase) {
        return hitSpectrum.containsKey(testCase);
    }

    public boolean containsValue(HitVector hitVector) {
        return hitSpectrum.containsValue(hitVector);
    }

    public HitVector remove(TestCase testCase) {
        return hitSpectrum.remove(testCase);
    }

    public void clear() {
        hitSpectrum.clear();
    }

    public HashMap<TestCase, HitVector> getHitSpectrum() {
        return hitSpectrum;
    }

    public boolean isHit(Component component, TestCase testCase) {
        return hitSpectrum.get(testCase).isHit(component);
    }

    public void filterComponents(FilterStrategy filterStrategy) {
        logger.info("Filtering components");
        List<Component> allComponents = new ArrayList<>(getComponents(true));
        filterStrategy.filterComponents(hitSpectrum, allComponents);
    }

    public void reconstructTraces(Reconstruction reconstruction) throws ReconstructionException {
        int counter = 1;
        List<TestCase> tests = new ArrayList<>(hitSpectrum.keySet());
        executionGraphHashMap = new HashMap<>();
        for (TestCase test : tests) {
            logger.info("\033[33m [" + counter + "/" + tests.size() + "] \033[0m Reconstructing trace for test case: " + test.toString());
            HitVector hitVector = hitSpectrum.get(test);
            logger.debug("Hit vector before reconstruction: " + hitVector.toString());
            hitVector = reconstruction.reconstruct(hitVector, test);
            logger.debug("Hit vector after reconstruction: " + hitVector.toString());
            hitSpectrum.put(test, hitVector);
            // TODO: Remove the hashmap for execution graph
            executionGraphHashMap.put(test, reconstruction.getExecutionGraph());
            logger.debug("Reconstruction done.");
            counter++;
        }

        //for (Component component : this.getFilteredComponents().stream().filter(component -> this.getComponents(false).contains(component)).collect(Collectors.toList())) {
        //    for (TestCase testCase : hitSpectrum.keySet()) {
        //        Hit hit = hitSpectrum.get(testCase).getHit(component);
        //        if (hit.getNumberOfHits() == -1) {
        //            hitSpectrum.get(testCase).getHit(component).setNumberOfHits(0);
        //        }
        //    }
        //}
    }

    public Map<TestCase, Boolean> isGroundTruthPresentInExecutionGraphInHowManyTests(ReconstructionApproach reconstruction, GroundTruth groundTruth) throws ReconstructionException {
        int counter = 1;
        Map<TestCase, Boolean> result = new HashMap<>();
        List<TestCase> tests = new ArrayList<>(hitSpectrum.keySet());
        for (TestCase test : tests) {
            logger.info("\033[33m [" + counter + "/" + tests.size() + "] \033[0m Reconstructing trace for test case: " + test.toString());
            HitVector hitVector = hitSpectrum.get(test);
            logger.debug("Hit vector before reconstruction: " + hitVector.toString());
            hitVector = reconstruction.reconstruct(hitVector, test);
            result.put(test, reconstruction.getExecutionGraph().nodes.values().stream().filter(node -> groundTruth.getComponents().contains(node.toComponent())).anyMatch(node -> true));
            counter++;
        }
        return result;
    }

    public Map<TestCase, Triple<Boolean, Boolean, Float>> queryReconstructTraces(ReconstructionApproach reconstruction, GroundTruth groundTruth) throws ReconstructionException {
        int counter = 1;
        Map<TestCase, Triple<Boolean, Boolean, Float>> result = new HashMap<>();
        List<TestCase> tests = hitSpectrum.keySet().stream().filter(testCase -> testCase.hasFailed()).collect(Collectors.toList());
        for (TestCase test : tests) {
            logger.info("\033[33m [" + counter + "/" + tests.size() + "] \033[0m Reconstructing trace for test case: " + test.toString());
            HitVector hitVector = hitSpectrum.get(test);
            logger.debug("Hit vector before reconstruction: " + hitVector.toString());
            result.put(test, reconstruction.reconstructionQuery(hitVector, test, groundTruth));
            counter++;
        }
        return result;
    }

    public float getProbabilityOfHit(Component component, TestCase testCase) {
        return hitSpectrum.get(testCase).getHit(component).getProbability();
    }

    public float calculateDistanceBetween(HitSpectrum hitSpectrum) {
        float distance = 0;
        int counter = 0;
        for (TestCase testCase : hitSpectrum.getTests()) {
            for (Component component : hitSpectrum.getComponents(true)) {
                distance += Math.pow(this.getProbabilityOfHit(component, testCase) - hitSpectrum.getProbabilityOfHit(component, testCase), 2);
                counter += 1;
            }
        }
        return distance/counter;
    }

    public ExecutionGraph getExecutionGraph(TestCase testCase) {
        if (executionGraphHashMap == null) {
            return null;
        }
        return executionGraphHashMap.get(testCase);
    }
}

