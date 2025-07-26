package org.diagnosis.evaluation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public abstract class Metrics implements Iterable<Metric>{

    List<Metric> metrics;

    public Metrics() {
        metrics = new ArrayList<>();
    }

    @Override
    public Iterator<Metric> iterator() {
        return metrics.iterator();
    }

    @Override
    public void forEach(Consumer<? super Metric> action) {
        metrics.forEach(action);
    }

    @Override
    public Spliterator<Metric> spliterator() {
        return metrics.spliterator();
    }

    public void calculateAll() {
        for (Metric metric : metrics) {
            metric.setValue(metric.calculate());
        }
    }

    public double get(String metricName) {
        for (Metric metric : metrics) {
            if (metric.getName().equals(metricName)) {
                return metric.getValue();
            }
        }
        return -1;
    }
}
