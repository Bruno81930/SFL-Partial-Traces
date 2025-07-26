package org.diagnosis.algorithms.reconstruction.graph.builder;

import org.diagnosis.algorithms.reconstruction.graph.ExecutionGraph;
import org.diagnosis.algorithms.reconstruction.graph.nodes.Node;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;

public class BodyHandler extends ElementHandler{
    public BodyHandler(CtElement element) {
        super(element);
    }

    @Override
    public Node process(Node parentNode, ExecutionGraph executionGraph) {
        CtBlock<?> block = (CtBlock<?>) element;
        Node lastNode = parentNode;
        for (CtStatement statement : block.getStatements()) {
            ElementHandler handler = ElementHandlerFactory.getHandler(statement);
            lastNode = handler.process(lastNode, executionGraph);
        }
        return lastNode;
    }
}
