package org.diagnosis.algorithms.reconstruction.graph.builder;

import org.diagnosis.algorithms.reconstruction.graph.ExecutionGraph;
import org.diagnosis.algorithms.reconstruction.graph.nodes.Node;
import org.diagnosis.algorithms.reconstruction.graph.nodes.NopNode;
import spoon.reflect.code.CtLoop;
import spoon.reflect.declaration.CtElement;

public class LoopHandler extends ElementHandler {
    public LoopHandler(CtElement element) {
        super(element);
    }

    @Override
    public Node process(Node parentNode, ExecutionGraph executionGraph) {
        CtLoop loop = (CtLoop) element;
        Node endLoopNode;

        Node emptyNode = new NopNode();

        executionGraph.addNode(emptyNode);
        executionGraph.addEdge(parentNode, emptyNode);

        if (loop.getBody() != null) {
            endLoopNode = ElementHandlerFactory.getHandler(loop.getBody()).process(parentNode, executionGraph);
            executionGraph.addNode(endLoopNode);
            executionGraph.addEdge(endLoopNode, emptyNode);
        }

        return emptyNode;
    }
}
