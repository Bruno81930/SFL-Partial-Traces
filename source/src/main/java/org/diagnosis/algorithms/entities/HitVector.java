package org.diagnosis.algorithms.entities;

import org.diagnosis.algorithms.reconstruction.graph.ExecutionGraph;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class HitVector implements Collection<Hit>, Iterable<Hit>, Cloneable, Serializable {

    private final List<Hit> hits;

    public HitVector() {
        this.hits = new ArrayList<>();
    }

    public HitVector(Map<Component, Float> componentMap) {
        this.hits = componentMap.entrySet().stream()
                .map(entry -> new Hit(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public Set<Component> getComponents() {
        Set<Component> components = new HashSet<>();
        for (Hit hit : hits) {
            if (hit.getNumberOfHits() > -1) {
                components.add(hit.getComponent());
            }
        }
        return components;
    }

    public Set<Component> getHitComponents() {
        Set<Component> components = new HashSet<>();
        for (Hit hit : hits) {
            if (hit.getNumberOfHits() > 0) {
                components.add(hit.getComponent());
            }
        }
        return components;
    }

    public Set<Component> getFilteredComponents() {
        Set<Component> components = new HashSet<>();
        for (Hit hit : hits) {
            if (hit.getNumberOfHits() == -1) {
                components.add(hit.getComponent());
            }
        }
        return components;
    }

    public Collection<? extends Component> getTraces() {
        Set<Component> traces = new HashSet<>();
        for (Hit hit : hits) {
            if (hit.isHit()) {
                traces.add(hit.getComponent());
            }
        }
        return traces;
    }

    public void filterComponentsGivenWhichToKeep(Set<Component> componentsToKeep) {
        hits.stream()
                .filter(hit -> !componentsToKeep.contains(hit.getComponent()))
                .forEach(hit -> hit.setNumberOfHits(-1));
    }

    public void filterComponentsGivenWhichToRemove(Set<Component> componentsToRemove) {
        hits.stream()
                .filter(hit -> componentsToRemove.contains(hit.getComponent()))
                .forEach(hit -> hit.setNumberOfHits(-1));
    }

    public HitVector filterComponentsToKeep(Set<Component> components) {
        HitVector filteredHitVector = new HitVector();
        for (Hit hit : hits) {
            if (components.contains(hit.getComponent())) {
                filteredHitVector.add(hit);
            }
        }
        return filteredHitVector;
    }

    @Override
    public int size() {
        return hits.size();
    }

    @Override
    public boolean isEmpty() {
        return hits.isEmpty();
    }

    public boolean isHit(Component component) {
        for (Hit hit : hits) {
            if (hit.getComponent().equals(component) && hit.isHit()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HitVector)) return false;
        HitVector hitVector = (HitVector) o;
        return hitVector.hits.stream()
                .allMatch(oHit -> hits.stream()
                        .anyMatch(hit -> hit.equals(oHit) && hit.getNumberOfHits() == oHit.getNumberOfHits()));
    }

    public List<Hit> getDifferentHits(HitVector other) {
        List<Hit> differentHits = new ArrayList<>();
        for (Hit hit : hits) {
            if (other.stream().anyMatch(otherHit -> otherHit.equals(hit) && (otherHit.isHit() && hit.isMiss()) || (otherHit.isMiss() && hit.isHit()))) {
                differentHits.add(hit);
            }
        }
        return differentHits;
    }

    @Override
    public boolean contains(Object o) {
        return hits.contains(o);
    }

    @Override
    public Iterator<Hit> iterator() {
        return hits.iterator();
    }

    @Override
    public Object[] toArray() {
        return hits.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return hits.toArray(a);
    }

    @Override
    public boolean add(Hit hit) {
        //TODO: Fix this
        if (hits.contains(hit)) {
            if (hits.get(hits.indexOf(hit)).getNumberOfHits() == -1) {
                hits.get(hits.indexOf(hit)).setNumberOfHits(0);
            }
            hits.get(hits.indexOf(hit)).addNumberOfHits(hit.getNumberOfHits());
            return true;
        }
        return hits.add(hit);
    }

    public boolean addPercentagesFromHit(Hit hit) {
        if (hits.contains(hit)) {
            float currentPercentage = hits.get(hits.indexOf(hit)).getNumberOfHits();
            hits.get(hits.indexOf(hit)).setHitPercentage(Math.max(currentPercentage, hit.getNumberOfHits()));
            return true;
        }
        return hits.add(hit);
    }

    public void fillPartialHits() {
        for (Hit hit : hits) {
            if (hit.getNumberOfHits() == -1) {
                hit.setNumberOfHits(0);
            }
        }
    }

    @Override
    public boolean remove(Object o) {
        return hits.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return hits.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Hit> c) {
        return hits.addAll(c);
    }

    public void addAllClones(Collection<? extends Hit> c) {
        hits.addAll(c.stream().map(Hit::clone).collect(Collectors.toList()));
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return hits.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return hits.retainAll(c);
    }

    @Override
    public void clear() {
        hits.clear();
    }

    @Override
    public HitVector clone() {
        try {
            super.clone();
            HitVector hitVectorClone = new HitVector();
            hitVectorClone.addAllClones(hits);
            return hitVectorClone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public Map<Hit, Boolean> getDeterminedHits() {
        return hits.stream().filter(hit -> hit.getNumberOfHits() >= 0).collect(Collectors.toMap(hit -> hit, Hit::isHit));
    }

    public List<Hit> getHits() {
        return hits;
    }

    public List<String> getPackageNames() {
        Set<String> packageNames = new HashSet<>();
        for (Hit hit : hits) {
            packageNames.add(hit.getPackageName());
        }
        return new ArrayList<>(packageNames);
    }

    public Map<Component, Float> getClone() {
        Map<Component, Float> clone = new HashMap<>();
        for (Hit hit : hits) {
            clone.put(hit.getComponent(), hit.getNumberOfHits());
        }
        return clone;
    }

    public float getHits(Component component) {
        for (Hit hit : hits) {
            if (hit.getComponent().equals(component)) {
                return hit.getNumberOfHits();
            }
        }
        return (float) -1;
    }

    public Hit getHit(Component component) {
        for (Hit hit : hits) {
            if (hit.getComponent().equals(component)) {
                return hit;
            }
        }
        return null;
    }

    public void addAverageCounterForPartialComponentsInRandomWalksFromHit(Hit hit) {
        for (Hit currentHit : hits) {
            if (currentHit.getComponent().equals(hit.getComponent())) {
                currentHit.setAverageCounterForPartialComponentsInRandomWalks(hit.getAverageCounterForPartialComponentsInRandomWalks());
            }
        }
    }

    public double getAverageComponentsInRandomWalks(Component component) {
        return hits.stream()
                .filter(hit -> hit.getComponent().equals(component))
                .mapToDouble(Hit::getAverageCounterForPartialComponentsInRandomWalks)
                .average()
                .orElse(-1);
    }

    public HitVector filterFromExecutionGraph(ExecutionGraph executionGraph) {
        HitVector filteredHitVector = new HitVector();
        for (Hit hit : hits) {
            if (executionGraph.isComponentInGraph(hit.getComponent())) {
                filteredHitVector.add(hit);
            }
        }
        return filteredHitVector;
    }

    public HitVector filterOnlyFiltered() {
        HitVector filteredHitVector = new HitVector();
        for (Hit hit : hits) {
            if (hit.getNumberOfHits() == -1) {
                filteredHitVector.add(hit);
            }
        }
        return filteredHitVector;
    }
}
