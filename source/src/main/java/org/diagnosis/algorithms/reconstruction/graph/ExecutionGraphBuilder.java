package org.diagnosis.algorithms.reconstruction.graph;

import org.diagnosis.algorithms.entities.HitVector;
import org.diagnosis.algorithms.reconstruction.graph.builder.ElementHandlerFactory;
import org.diagnosis.algorithms.reconstruction.graph.nodes.MethodNode;
import org.diagnosis.algorithms.reconstruction.graph.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.reflect.declaration.CtMethod;

public class ExecutionGraphBuilder {

    public static ExecutionGraph build(CtMethod<?> entryMethod, HitVector hitVector) {
        Logger logger = LoggerFactory.getLogger(ExecutionGraphBuilder.class);

        Node root = new MethodNode(entryMethod);
        logger.debug("Root node: " + root);

        logger.debug("Creating execution graph");
        ExecutionGraph executionGraph = new ExecutionGraph(root);

        logger.debug("Adding root node to the execution graph");
        executionGraph.addNode(root);

        logger.debug("Building execution graph");
        ElementHandlerFactory.getHandler(entryMethod).process(root, executionGraph);

        logger.debug("Removing NOP nodes");
        executionGraph.removeNOPNodes();

        logger.debug("Pruning Graph");
        logger.debug("Number of nodes before filtering: " + executionGraph.graph.vertexSet().size() + " nodes");
        executionGraph.pruneGraph(hitVector);
        logger.debug("Number of nodes after filtering: " + executionGraph.graph.vertexSet().size() + " nodes");

        if (executionGraph.nodes.size() == 0) {
            logger.debug("No nodes in the graph");
            return executionGraph;
        }

        logger.debug("Adding start and end nodes");
        executionGraph.addStartEndNodes();
        return executionGraph;
    }

}
