package org.diagnosis.algorithms.reconstruction.graph.builder;

import org.diagnosis.algorithms.reconstruction.graph.ExecutionGraph;
import org.diagnosis.algorithms.reconstruction.graph.nodes.Node;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;

public class MethodHandler extends ElementHandler {
    public MethodHandler(CtElement element) {
        super(element);
    }

    @Override
    public Node process(Node parentNode, ExecutionGraph executionGraph) {
        CtExecutable<?> method = (CtExecutable<?>) element;
        executionGraph.addToStack(parentNode);
        if (method.getBody() != null) {
            parentNode = ElementHandlerFactory.getHandler(method.getBody()).process(parentNode, executionGraph);
        }
        executionGraph.popFromStack();
        return parentNode;
    }
}
