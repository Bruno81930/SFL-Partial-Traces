package org.diagnosis.algorithms.reconstruction.graph.builder;

import org.diagnosis.algorithms.reconstruction.graph.ExecutionGraph;
import org.diagnosis.algorithms.reconstruction.graph.nodes.ConstructorCallNode;
import org.diagnosis.algorithms.reconstruction.graph.nodes.Node;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;

public class ConstructorCallHandler extends ElementHandler {
    public ConstructorCallHandler(CtElement element) {
        super(element);
    }

    @Override
    public Node process(Node parentNode, ExecutionGraph executionGraph) {
        CtConstructorCall<?> constructorCall = (CtConstructorCall<?>) element;
        CtConstructor<?> constructor = (CtConstructor<?>) constructorCall.getExecutable().getExecutableDeclaration();
        Node returnConstructorCall = parentNode;
        String callerNode = executionGraph.peekStack();
        if (callerNode == null) {
            callerNode = parentNode.getSignature();
        }
        Node newConstructorCall = new ConstructorCallNode(constructorCall, callerNode, executionGraph.sizeStack(), executionGraph.hashStack());

        if (executionGraph.isNotInStack(newConstructorCall)) {
            returnConstructorCall = newConstructorCall;
            executionGraph.addNode(newConstructorCall);
            executionGraph.addEdge(parentNode, newConstructorCall);
            executionGraph.addToStack(newConstructorCall);
            if (constructor != null && constructor.getBody() != null) {
                returnConstructorCall = ElementHandlerFactory.getHandler(constructor.getBody()).process(newConstructorCall, executionGraph);
            }
            executionGraph.popFromStack();
        }

        return returnConstructorCall;
    }
}
