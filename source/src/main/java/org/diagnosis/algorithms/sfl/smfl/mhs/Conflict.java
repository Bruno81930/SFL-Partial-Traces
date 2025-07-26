package org.diagnosis.algorithms.sfl.smfl.mhs;

import org.diagnosis.algorithms.entities.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Conflict implements Set<Component> {

    private final HashSet<Component> minimalHittingSet;

    public Conflict() {
        this.minimalHittingSet = new HashSet<>();
    }

    @Override
    public int size() {
        return minimalHittingSet.size();
    }

    @Override
    public boolean isEmpty() {
        return minimalHittingSet.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return minimalHittingSet.contains(o);
    }

    @Override
    public Iterator<Component> iterator() {
        return minimalHittingSet.iterator();
    }

    @Override
    public Object[] toArray() {
        return minimalHittingSet.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return minimalHittingSet.toArray(a);
    }

    @Override
    public boolean add(Component integer) {
        return minimalHittingSet.add(integer);
    }

    @Override
    public boolean remove(Object o) {
        return minimalHittingSet.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return minimalHittingSet.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Component> c) {
        return minimalHittingSet.addAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return minimalHittingSet.retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return minimalHittingSet.removeAll(c);
    }

    @Override
    public void clear() {
        minimalHittingSet.clear();
    }

}
