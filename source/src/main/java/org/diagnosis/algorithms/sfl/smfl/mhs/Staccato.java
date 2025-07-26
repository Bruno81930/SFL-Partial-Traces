package org.diagnosis.algorithms.sfl.smfl.mhs;

import org.diagnosis.algorithms.entities.Component;
import org.diagnosis.algorithms.sfl.ssfl.SimilarityCoefficients;

import java.util.*;
import java.util.stream.Collectors;

public class Staccato {
    SimilarityCoefficients coefficients;
    float maximumSeenComponents;
    int maximumSizeOfMinimalHittingSets;

    public Staccato(SimilarityCoefficients coefficients) {
        this.coefficients = coefficients;
        this.maximumSeenComponents = 0.8f;
        this.maximumSizeOfMinimalHittingSets = 1000;
    }

    public Set<MinimalHittingSet> execute(HitMatrix hitMatrix, ErrorVector errorVector) {
        // Initialization Phase
        int numberOfComponents = hitMatrix.getNumberOfComponents();
        Set<MinimalHittingSet> minimalHittingSets = new HashSet<>();
        Set<Conflict> conflicts = hitMatrix.getConflicts(errorVector);
        if (conflicts.size() == 0) {
            return minimalHittingSets;
        }
        List<Component> rankedComponent = rankComponents(hitMatrix);
        float seen = 0.0f;

        // Adding minimum hitting sets: components that are in all failing tests
        for (Component component : hitMatrix.components) { // Steps 5-12
            if (coefficients.N11(component) == conflicts.size()) {
                MinimalHittingSet minimalHittingSet = new MinimalHittingSet();
                minimalHittingSet.add(component);
                minimalHittingSets.add(minimalHittingSet);
                hitMatrix.strip(component);
                rankedComponent.remove(component);
                seen += 1.0f/numberOfComponents;
            }
        }


        while(!rankedComponent.isEmpty() && seen < maximumSeenComponents && minimalHittingSets.size() <= maximumSizeOfMinimalHittingSets) {
            Component component = rankedComponent.get(0);
            rankedComponent.remove(component);
            seen += 1.0f/numberOfComponents;
            HitMatrix reducedHitMatrix = hitMatrix.clone();
            ErrorVector reducedErrorVector = errorVector.clone();
            reducedHitMatrix.strip(component, reducedErrorVector);
            Set<MinimalHittingSet> reducedMinimalHittingSets = execute(reducedHitMatrix, reducedErrorVector);
            while (!reducedMinimalHittingSets.isEmpty()) {
                MinimalHittingSet reducedMinimalHittingSet = reducedMinimalHittingSets.iterator().next();
                reducedMinimalHittingSets.remove(reducedMinimalHittingSet);
                HashSet<MinimalHittingSet> clonedMinimalHittingSets = new HashSet<>();
                for (MinimalHittingSet minimalHittingSet : minimalHittingSets) {
                    MinimalHittingSet clonedReducedMinimalHittingSet = reducedMinimalHittingSet.combined(minimalHittingSet);
                    clonedMinimalHittingSets.add(clonedReducedMinimalHittingSet);
                }
                minimalHittingSets.addAll(clonedMinimalHittingSets);
            }

        }

        return minimalHittingSets;
    }

    private List<Component> rankComponents(HitMatrix hitMatrix) {
        Map<Component, Double> rankingProbabilities = new LinkedHashMap<>();
        for (Component component : hitMatrix.components) {
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

}
