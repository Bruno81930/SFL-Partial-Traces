package org.diagnosis.algorithms.sfl.smfl.mhs;

import org.diagnosis.algorithms.entities.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MinimalHittingSet implements Set<Component>, Cloneable {

    private final HashSet<Component> minimalHittingSet;

    public MinimalHittingSet() {
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
    public boolean add(Component component) {
        return minimalHittingSet.add(component);
    }

    @Override
    public boolean remove(Object o) {
        return minimalHittingSet.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        if (c instanceof Component) {
            return minimalHittingSet.containsAll(c);
        }
        return false;
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

    public MinimalHittingSet combined(MinimalHittingSet minimalHittingSet) {
        MinimalHittingSet combined = new MinimalHittingSet();
        combined.addAll(this);
        combined.addAll(minimalHittingSet);
        return combined;
    }
}
