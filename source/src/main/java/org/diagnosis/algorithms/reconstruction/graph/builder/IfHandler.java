package org.diagnosis.algorithms.reconstruction.graph.builder;

import org.diagnosis.algorithms.reconstruction.graph.ExecutionGraph;
import org.diagnosis.algorithms.reconstruction.graph.nodes.Node;
import org.diagnosis.algorithms.reconstruction.graph.nodes.NopNode;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtIf;
import spoon.reflect.declaration.CtElement;

public class IfHandler extends ElementHandler {
    public IfHandler(CtElement element) {
        super(element);
    }

    @Override
    public Node process(Node parentNode, ExecutionGraph executionGraph) {
        CtIf conditional = (CtIf) element;
        CtBlock<?> thenStatement = conditional.getThenStatement();
        CtBlock<?> elseStatement= conditional.getElseStatement();

        Node thenMethod;
        Node elseMethod;
        Node emptyNode = new NopNode();
        executionGraph.addNode(emptyNode);
        executionGraph.addEdge(parentNode, emptyNode);
        if (thenStatement != null) {
            thenMethod = ElementHandlerFactory.getHandler(thenStatement).process(parentNode, executionGraph);
            executionGraph.addNode(thenMethod);
            executionGraph.addEdge(thenMethod, emptyNode);
        }

        if (elseStatement != null) {
            elseMethod = ElementHandlerFactory.getHandler(elseStatement).process(parentNode, executionGraph);
            executionGraph.addNode(elseMethod);
            executionGraph.addEdge(elseMethod, emptyNode);
        }

        return emptyNode;
    }
}
