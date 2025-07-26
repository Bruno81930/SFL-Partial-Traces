package org.diagnosis.algorithms.reconstruction.graph.builder;

import org.diagnosis.algorithms.reconstruction.graph.ExecutionGraph;
import org.diagnosis.algorithms.reconstruction.graph.nodes.AnonymousNode;
import org.diagnosis.algorithms.reconstruction.graph.nodes.Node;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtElement;

public class AnonymousCallHandler extends ElementHandler {
    public AnonymousCallHandler(CtElement element) {
        super(element);
    }

    @Override
    public Node process(Node parentNode, ExecutionGraph executionGraph) {
        CtAnonymousExecutable anonymousExecutable = (CtAnonymousExecutable) element;
        Node staticInitNode = new AnonymousNode(anonymousExecutable, parentNode.getSignature(), executionGraph.sizeStack(), executionGraph.hashStack());
        executionGraph.addNode(staticInitNode);
        executionGraph.addEdge(parentNode, staticInitNode);

        if (anonymousExecutable.getBody() != null) {
            staticInitNode = ElementHandlerFactory.getHandler(anonymousExecutable.getBody()).process(staticInitNode, executionGraph);
        }
        return staticInitNode;
    }
}
