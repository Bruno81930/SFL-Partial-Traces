package org.diagnosis.algorithms.reconstruction;

import org.apache.commons.lang3.tuple.Triple;
import org.diagnosis.algorithms.entities.HitSpectrum;
import org.diagnosis.algorithms.entities.HitVector;
import org.diagnosis.algorithms.entities.TestCase;
import org.diagnosis.algorithms.reconstruction.graph.ExecutionGraph;
import org.diagnosis.algorithms.reconstruction.graph.ExecutionGraphBuilder;
import org.diagnosis.algorithms.reconstruction.techniques.bruteforce.inference.InferenceStrategy;
import org.diagnosis.algorithms.reconstruction.techniques.graph.GraphTechnique;
import org.diagnosis.algorithms.reconstruction.techniques.graph.GraphWeightedTechnique;
import org.diagnosis.evaluation.ground_truth.GroundTruth;
import org.diagnosis.algorithms.reconstruction.graph.nodes.Node;
import org.jgrapht.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.reflect.declaration.CtMethod;
import tech.tablesaw.columns.numbers.DoubleParser;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

public class ReconstructionApproach implements Reconstruction {
    final InferenceStrategy inferenceStrategy;
    final String dataPath;
    final String[] sources;
    final SourceModel sourceModel;
    ExecutionGraph executionGraph;
    HitSpectrum fullTraceDebug;
    boolean weighted;

    // Used for debugging
    Path executionGraphDir;
    GroundTruth groundTruth;

    Logger logger = LoggerFactory.getLogger(ReconstructionApproach.class);

    public ReconstructionApproach(InferenceStrategy inferenceStrategy, String dataPath, String[] sources, boolean weighted, SourceModel sourceModel) {
        this.inferenceStrategy = inferenceStrategy;
        this.dataPath = dataPath;
        this.sources = sources;
        this.sourceModel = sourceModel;
        this.weighted = weighted;
    }

    public void setFullTraceDebug(HitSpectrum fullTraceDebug) {
        this.fullTraceDebug = fullTraceDebug;
    }

    public void setExecutionGraphDir(Path executionGraphDir) {
        this.executionGraphDir = executionGraphDir;
    }

    public void setGroundTruth(GroundTruth groundTruth) {
        this.groundTruth = groundTruth;
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
        executionGraph = ExecutionGraphBuilder.build(entryMethod, hitVector);
        //executionGraph.setFullTrace(fullTraceDebug.get(testCase));

        if (executionGraphDir != null) {
            logger.debug("Saving the execution graph");
            try {
                executionGraph.save(Paths.get(executionGraphDir.toString(), testCase + ".dot"), groundTruth);
            } catch (IOException e) {
                throw new ReconstructionException("Failed to save the execution graph", e);
            }
        }

        GraphTechnique graphTechnique;
        if (weighted) {
            graphTechnique = new GraphWeightedTechnique(executionGraph, hitVector);
        } else {
            graphTechnique = new GraphTechnique(executionGraph);
        }

        logger.debug("Reconstructing the execution graph");
        return graphTechnique.reconstruct(hitVector);
    }

    public ExecutionGraph getExecutionGraph() {
        return executionGraph;
    }

    public Triple<Boolean, Boolean, Float> reconstructionQuery(HitVector hitVector, TestCase testCase, GroundTruth groundTruth) throws ReconstructionException {
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
        executionGraph = ExecutionGraphBuilder.build(entryMethod, hitVector);
        GraphTechnique graphTechnique = new GraphTechnique(executionGraph);
        Boolean isGroundTruthInExecutionPath = groundTruth.getComponents().stream().allMatch(component -> executionGraph.isComponentInGraph(component));
        if (!isGroundTruthInExecutionPath) {
            return Triple.of(false, false, 0.0f);
        }
        List<Node> groundTruthNodes = groundTruth.getComponents().stream().map(component -> executionGraph.getNode(component)).collect(Collectors.toList());
        Boolean isGroundTruthCoveredByAllPaths = groundTruthNodes.stream().allMatch(node -> graphTechnique.isNodeCovered(node, hitVector));
        OptionalDouble optionalCoverage = groundTruthNodes.stream().map(node -> graphTechnique.getGraphCoverage(node, hitVector)).mapToDouble(f -> f).average();
        if (optionalCoverage.isPresent()) {
            return Triple.of(true, isGroundTruthCoveredByAllPaths, (float) optionalCoverage.getAsDouble());
        } else {
            return Triple.of(true, isGroundTruthCoveredByAllPaths, 0.0f);
        }


    }

}