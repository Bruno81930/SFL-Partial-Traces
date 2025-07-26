package org.diagnosis.algorithms.reconstruction.graph.builder;

import org.diagnosis.algorithms.reconstruction.graph.ExecutionGraph;
import org.diagnosis.algorithms.reconstruction.graph.nodes.Node;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;

public class StatementHandler extends ElementHandler {
    public StatementHandler(CtElement element) {
        super(element);
    }

    @Override
    public Node process(Node parentNode, ExecutionGraph executionGraph) {
        CtStatement statement = (CtStatement) element;
        for (CtElement child : statement.getDirectChildren()) {
            parentNode = ElementHandlerFactory.getHandler(child).process(parentNode, executionGraph);
        }
        return parentNode;
    }
}
