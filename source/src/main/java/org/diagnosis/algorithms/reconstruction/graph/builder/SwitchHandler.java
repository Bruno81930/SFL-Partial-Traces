package org.diagnosis.algorithms.reconstruction.graph.builder;

import org.diagnosis.algorithms.reconstruction.graph.ExecutionGraph;
import org.diagnosis.algorithms.reconstruction.graph.nodes.Node;
import org.diagnosis.algorithms.reconstruction.graph.nodes.NopNode;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.declaration.CtElement;

import java.util.List;

public class SwitchHandler extends ElementHandler {
    public SwitchHandler(CtElement element) {
        super(element);
    }

    @Override
    public Node process(Node parentNode, ExecutionGraph executionGraph) {
        CtSwitch<?> switchBlock = (CtSwitch<?>) element;

        Node endCaseNode;
        Node switchEmptyNode = new NopNode();
        executionGraph.addNode(switchEmptyNode);
        executionGraph.addEdge(parentNode, switchEmptyNode);

        for (CtCase<?> caseBlock : switchBlock.getCases()) {
            List<CtStatement> statements = caseBlock.getStatements();
            if (statements != null) {
                endCaseNode = ElementHandlerFactory.getHandler(caseBlock).process(parentNode, executionGraph);
                executionGraph.addNode(endCaseNode);
                executionGraph.addEdge(endCaseNode, switchEmptyNode);
            }
        }

        return switchEmptyNode;
    }
}
