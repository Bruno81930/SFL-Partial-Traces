package org.diagnosis.algorithms.reconstruction.graph.builder;

import org.diagnosis.algorithms.reconstruction.graph.ExecutionGraph;
import org.diagnosis.algorithms.reconstruction.graph.nodes.Node;
import org.diagnosis.algorithms.reconstruction.graph.nodes.NopNode;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.declaration.CtElement;

public class SynchronizedHandler extends ElementHandler {
    public SynchronizedHandler(CtElement element) {
        super(element);
    }

    @Override
    public Node process(Node parentNode, ExecutionGraph executionGraph) {
        CtSynchronized synchronizedStatement = (CtSynchronized) element;
        Node synchronizedEndNode;

        CtBlock<?> synchronizedBody = synchronizedStatement.getBlock();
        Node emptyNode = new NopNode();

        executionGraph.addNode(emptyNode);
        executionGraph.addEdge(parentNode, emptyNode);

        if (synchronizedBody != null) {
            synchronizedEndNode = ElementHandlerFactory.getHandler(synchronizedBody).process(parentNode, executionGraph);
            executionGraph.addNode(synchronizedEndNode);
            executionGraph.addEdge(synchronizedEndNode, emptyNode);
        }
        return emptyNode;
    }
}
