package org.diagnosis.algorithms.reconstruction.graph.builder;

import org.diagnosis.algorithms.reconstruction.graph.ExecutionGraph;
import org.diagnosis.algorithms.reconstruction.graph.nodes.MethodCallNode;
import org.diagnosis.algorithms.reconstruction.graph.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;

public class InvocationHandler extends ElementHandler {
    public InvocationHandler(CtElement element) {
        super(element);
    }

    Logger logger = LoggerFactory.getLogger(InvocationHandler.class);

    @Override
    public Node process(Node parentNode, ExecutionGraph executionGraph) {
        CtInvocation<?> invocation = (CtInvocation<?>) element;
        CtExecutable<?> invokedMethod = invocation.getExecutable().getExecutableDeclaration();
        Node returnMethod = parentNode;
        String callerNode = executionGraph.peekStack();
        if (callerNode == null) {
            callerNode = parentNode.getSignature();
        }
        Node newMethod = new MethodCallNode(invocation, callerNode, executionGraph.sizeStack(), executionGraph.hashStack());

        if (executionGraph.isNotInStack(newMethod)) {
            returnMethod = newMethod;
            executionGraph.addNode(newMethod);
            executionGraph.addEdge(parentNode, newMethod);
            executionGraph.addToStack(newMethod);
            if (invokedMethod != null && invokedMethod.getBody() != null) {
                returnMethod = ElementHandlerFactory.getHandler(invokedMethod.getBody()).process(newMethod, executionGraph);
            }
            executionGraph.popFromStack();
        }

        return returnMethod;
    }
}
