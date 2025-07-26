package org.diagnosis.algorithms.reconstruction.techniques.bruteforce;

import org.diagnosis.algorithms.entities.HitVector;
import org.diagnosis.algorithms.reconstruction.graph.ExecutionGraph;
import org.diagnosis.algorithms.reconstruction.techniques.Technique;
import org.diagnosis.algorithms.reconstruction.techniques.bruteforce.inference.InferenceStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BruteForceTechnique implements Technique {
    private final ExecutionGraph executionGraph;

    private final Logger logger = LoggerFactory.getLogger(BruteForceTechnique.class);

    public BruteForceTechnique(ExecutionGraph executionGraph) {
        this.executionGraph = executionGraph;
    }

    @Override
    public HitVector reconstruct(HitVector hitVector) {
        logger.debug("Cloning the hit vector");
        HitVector reconstructedHitVector = new HitVector();
        reconstructedHitVector.addAll(hitVector);

        logger.debug("Extracting the paths from the execution graph: " + executionGraph.graph.vertexSet().size() + " nodes");
        List<ExecutionPath> paths = executionGraph.getPaths();

        logger.debug("Cleaning memory");
        System.gc();
        logger.debug("Finished cleaning memory");

        logger.debug("Inferring the hit vector");
        InferenceStrategy inferenceStrategy = new InferenceStrategy();
        inferenceStrategy.infer(paths, reconstructedHitVector);
        System.gc();
        logger.debug("Concluded extracting the hit vector");

        return reconstructedHitVector;
    }
}
