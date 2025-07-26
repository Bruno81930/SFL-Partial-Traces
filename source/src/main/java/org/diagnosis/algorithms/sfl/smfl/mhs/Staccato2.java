package org.diagnosis.algorithms.sfl.smfl.mhs;

import org.diagnosis.algorithms.entities.Component;
import org.diagnosis.algorithms.sfl.ssfl.SimilarityCoefficients;

import java.util.*;
import java.util.stream.Collectors;

public class Staccato2 {
    List<Component> components;
    SimilarityCoefficients coefficients;
    float maximumSeenComponents;
    int maximumSizeOfMinimalHittingSets;

    public Staccato2(List<Component> components, SimilarityCoefficients coefficients) {
        this.components = components;
        this.coefficients = coefficients;
        this.maximumSeenComponents = 0.8f;
        this.maximumSizeOfMinimalHittingSets = 100;
    }

    public Set<MinimalHittingSet> execute(boolean[][] hitMatrix, boolean[] errorVector, int numberOfComponents) {
        // Initialization Phase
        Set<MinimalHittingSet> minimalHittingSets = new HashSet<>();
        Set<Conflict> conflicts = getConflicts(hitMatrix, errorVector);
        List<Component> rankedComponent = rankComponents();
        float seen = 0.0f;

        // Adding minimum hitting sets: components that are in all failing tests
        for (int componentId = 0; componentId < numberOfComponents; componentId++) { // Steps 5-12
            if (coefficients.N11(this.components.get(componentId)) == conflicts.size()) {
                MinimalHittingSet minimalHittingSet = new MinimalHittingSet();
                minimalHittingSet.add(this.components.get(componentId));
                minimalHittingSets.add(minimalHittingSet);
                hitMatrix = stripComponent(hitMatrix, componentId);
                rankedComponent.remove(components.get(componentId));
                seen += 1.0f/numberOfComponents;
            }
        }


        while(!rankedComponent.isEmpty() && seen < maximumSeenComponents && minimalHittingSets.size() <= maximumSizeOfMinimalHittingSets) {
            Component component = rankedComponent.get(0);
            rankedComponent.remove(component);
            seen += 1.0f/numberOfComponents;

        }

        return minimalHittingSets;
    }

    private Set<Conflict> getConflicts(boolean[][] hitMatrix, boolean[] errorVector) {
        Set<Conflict> conflicts = new HashSet<>();

        for (int test = 0; test < hitMatrix.length; test++) {
            if (errorVector[test]) {
                Conflict conflict = new Conflict();
                for (int componentId = 0; componentId < hitMatrix[test].length; componentId++) {
                    if (hitMatrix[test][componentId]) {
                        conflict.add(null);//componentId);
                    }
                }
                if (!conflict.isEmpty()) {
                    conflicts.add(conflict);
                }
            }
        }

        return conflicts;
    }

    private List<Component> rankComponents() {
        Map<Component, Double> rankingProbabilities = new LinkedHashMap<>();
        for (Component component : this.components) {
            float n00 = (float) this.coefficients.N00(component);
            float n01 = (float) this.coefficients.N01(component);
            float n10 = (float) this.coefficients.N10(component);
            float n11 = (float) this.coefficients.N11(component);

            rankingProbabilities.put(component, this.coefficients.getFormula().getFormula().compute(n00, n01, n10, n11));
        }

        return rankingProbabilities.entrySet().stream()
                .sorted(Map.Entry.<Component, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private boolean[][] stripComponent(boolean[][] hitMatrix, int componentId) {
        boolean[][] newHitMatrix = new boolean[hitMatrix.length][hitMatrix[0].length - 1];
        for (int test = 0; test < hitMatrix.length; test++) {
            int newComponentId = 0;
            for (int oldComponentId = 0; oldComponentId < hitMatrix[test].length; oldComponentId++) {
                if (oldComponentId != componentId) {
                    newHitMatrix[test][newComponentId] = hitMatrix[test][oldComponentId];
                    newComponentId++;
                }
            }
        }
        return newHitMatrix;
    }
}
