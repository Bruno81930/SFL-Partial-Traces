package org.diagnosis.algorithms.reconstruction.graph.builder;

import org.diagnosis.algorithms.reconstruction.graph.ExecutionGraph;
import org.diagnosis.algorithms.reconstruction.graph.nodes.Node;
import spoon.reflect.declaration.CtElement;

public abstract class ElementHandler {

    protected CtElement element;

    public ElementHandler(CtElement element) {
        this.element = element;
    }

    public abstract Node process(Node parentNode, ExecutionGraph executionGraph);
}
