package org.diagnosis.algorithms.reconstruction;

import org.diagnosis.algorithms.entities.HitVector;
import org.diagnosis.algorithms.entities.TestCase;
import org.diagnosis.algorithms.reconstruction.graph.ExecutionGraph;
import org.diagnosis.algorithms.reconstruction.graph.ExecutionGraphBuilder;
import org.diagnosis.algorithms.reconstruction.techniques.probabilities.ProbabilitiesTechnique;
import org.diagnosis.algorithms.walker.InformedRandomWalkStrategy;
import org.diagnosis.algorithms.walker.InformedWalkStrategy;
import org.diagnosis.algorithms.walker.RandomWalkStrategy;
import org.diagnosis.algorithms.walker.WalkStrategy;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.reflect.declaration.CtMethod;

import java.util.HashMap;
import java.util.Map;

public class SFLPlusApproach implements Reconstruction {
    private final SourceModel sourceModel;
    private final int randomWalkNumber;
    private final WalkStrategy walkStrategy;

    Logger logger = LoggerFactory.getLogger(SFLPlusApproach.class);

    public SFLPlusApproach(String dataPath, String[] sources, int randomWalkNumber, WalkStrategy walkStrategy) {
        this.sourceModel = new SourceModel(dataPath, sources);
        this.randomWalkNumber = randomWalkNumber;
        this.walkStrategy = walkStrategy;
    }

    @Override
    public HitVector reconstruct(HitVector hitVector, TestCase testCase) throws ReconstructionException {
        CtMethod<?> entryMethod;

        try {
            logger.debug("Extracting the entry method");
            if (testCase.getPackageName().isEmpty()) {
                entryMethod = sourceModel.getEntryMethod(testCase.getClassName(), testCase.getMethodName());
            } else {
                entryMethod = sourceModel.getEntryMethod(testCase.getPackageName(), testCase.getClassName(), testCase.getMethodName());
            }
            logger.debug("Entry method: " + entryMethod.getSignature());
        } catch (SourceModelException e) {
            throw new ReconstructionException("Failed to extract the entry method", e);
        }

        logger.debug("Building the execution graph");
        ExecutionGraph executionGraph = ExecutionGraphBuilder.build(entryMethod, hitVector);
        if (executionGraph.nodes.isEmpty())
            return hitVector;

        if (walkStrategy instanceof InformedRandomWalkStrategy || walkStrategy instanceof InformedWalkStrategy) {
            executionGraph.calculateWeights(hitVector);
        }

        ProbabilitiesTechnique probabilitiesTechnique = new ProbabilitiesTechnique(executionGraph, randomWalkNumber, walkStrategy);


        return probabilitiesTechnique.reconstruct(hitVector);


    }

    @Override
    public ExecutionGraph getExecutionGraph() {
        return null;
    }
}
