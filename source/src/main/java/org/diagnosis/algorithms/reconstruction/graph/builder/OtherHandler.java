package org.diagnosis.algorithms.reconstruction.graph.builder;

import org.diagnosis.algorithms.reconstruction.graph.ExecutionGraph;
import org.diagnosis.algorithms.reconstruction.graph.nodes.Node;

public class OtherHandler extends ElementHandler {
    public OtherHandler() {
        super(null);
    }

    @Override
    public Node process(Node parentNode, ExecutionGraph executionGraph) {
        return parentNode;
    }
}
