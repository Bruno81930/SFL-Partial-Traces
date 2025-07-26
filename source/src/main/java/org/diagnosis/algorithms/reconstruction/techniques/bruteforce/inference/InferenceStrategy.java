package org.diagnosis.algorithms.reconstruction.techniques.bruteforce.inference;

import org.diagnosis.algorithms.entities.Component;
import org.diagnosis.algorithms.entities.Hit;
import org.diagnosis.algorithms.entities.HitVector;
import org.diagnosis.algorithms.reconstruction.techniques.bruteforce.ExecutionPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InferenceStrategy {
    List<ExecutionPath> executionPaths;
    HitVector hitVector;
    Map<ExecutionPath, Float> candidateScores;

    Logger logger = LoggerFactory.getLogger(InferenceStrategy.class);

    public InferenceStrategy() {
    }

    public void infer(List<ExecutionPath> executionPaths, HitVector hitVector) {
        this.executionPaths = executionPaths;
        this.hitVector = hitVector;

        logger.info("Getting candidate scores");
        Map<Component, Float> components = hitVector.getHits().stream()
                .collect(Collectors.toMap(Hit::getComponent, Hit::getNumberOfHits));
        logger.debug("Components: {}", components.size());
        Map<ExecutionPath, Float> candidateScores = this.executionPaths.parallelStream().collect(Collectors.toMap(
                item -> item,
                item -> this.rankExecutionPath(item, components)
        ));

        logger.info("Getting candidate execution paths");
        float maxScore = candidateScores.values().stream()
                .max(Float::compare)
                .orElse(Float.MIN_VALUE);

        List<List<Component>> candidates = candidateScores.entrySet().stream()
                .filter(entry -> entry.getValue() == maxScore)
                .map(Map.Entry::getKey)
                .map(ExecutionPath::getComponents)
                .collect(Collectors.toList());

        logger.info("Inferring hit vector");
        hitVector.stream()
                .filter(hit -> hit.getNumberOfHits() == -1)
                .forEach(hit -> inferHitVector(hit, candidates));
    }

    private float rankExecutionPath(ExecutionPath executionPath, Map<Component, Float> components) {
        long intersectedHitCount = executionPath.getComponents().stream()
                .filter(components::containsKey)
                .filter(component -> components.get(component) != -1)
                .count();

        return intersectedHitCount / (float) components.size();
    }

    private void inferHitVector(Hit hit, List<List<Component>> candidates) {
        long numberOfMatches = candidates
                .stream()
                .filter(candidate -> candidate.contains(hit.getComponent()))
                .count();
        int numberOfHits = (numberOfMatches == candidates.size()) ? 1 : 0;
        hit.setNumberOfHits(numberOfHits);
    }

    public String getName() {
        return "Baseline Inference";
    }
}
