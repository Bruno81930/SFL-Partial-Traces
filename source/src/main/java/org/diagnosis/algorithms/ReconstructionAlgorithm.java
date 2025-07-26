package org.diagnosis.algorithms;

import org.apache.commons.lang3.tuple.Triple;
import org.diagnosis.algorithms.entities.Component;
import org.diagnosis.algorithms.entities.HitSpectrum;
import org.diagnosis.algorithms.entities.HitVector;
import org.diagnosis.algorithms.entities.TestCase;
import org.diagnosis.algorithms.filter.FilterStrategy;
import org.diagnosis.algorithms.parser.ExecutionsParser;
import org.diagnosis.algorithms.reconstruction.ReconstructionApproach;
import org.diagnosis.algorithms.reconstruction.ReconstructionException;
import org.diagnosis.algorithms.reconstruction.SourceModel;
import org.diagnosis.algorithms.reconstruction.graph.ExecutionGraph;
import org.diagnosis.algorithms.reconstruction.techniques.bruteforce.inference.InferenceStrategy;
import org.diagnosis.evaluation.ground_truth.GroundTruth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ReconstructionAlgorithm extends Algorithm {

    private final String repositoryPath;
    private final FilterStrategy filterStrategy;
    private final String[] sources;
    private final ReconstructionApproach reconstructionApproach;
    private final HitSpectrum fullTrace;
    private boolean filteredTracesFlag = false;

    Logger logger = LoggerFactory.getLogger(ReconstructionAlgorithm.class);

    public ReconstructionAlgorithm(FilterStrategy filterStrategy, InferenceStrategy inferenceStrategy, String repositoryPath, String[] sources) {
        this(filterStrategy, inferenceStrategy, repositoryPath, sources, new SourceModel(repositoryPath, sources), false);
    }

    public ReconstructionAlgorithm(FilterStrategy filterStrategy, InferenceStrategy inferenceStrategy, String repositoryPath, String[] sources, boolean weighted) {
       this(filterStrategy, inferenceStrategy, repositoryPath, sources, new SourceModel(repositoryPath, sources), weighted);
    }

    public ReconstructionAlgorithm(FilterStrategy filterStrategy, InferenceStrategy inferenceStrategy, String repositoryPath, String[] sources, SourceModel sourceModel, boolean weighted) {
        this.repositoryPath = repositoryPath;
        this.filterStrategy = filterStrategy;
        this.sources = sources;
        this.reconstructionApproach = new ReconstructionApproach(inferenceStrategy, repositoryPath, sources, weighted, sourceModel);
        this.fullTrace = new HitSpectrum();
    }

    public void setExecutionGraphDir(Path executionGraphDir) throws IOException {
        try {
            Files.createDirectories(executionGraphDir);
        } catch (IOException e) {
            throw new IOException("Failed to create the execution graph directory", e);
        }
        this.reconstructionApproach.setExecutionGraphDir(executionGraphDir);
    }

    public void setGroundTruth(GroundTruth groundTruth) {
        this.reconstructionApproach.setGroundTruth(groundTruth);
    }

    public void setPartialTrace(HitSpectrum partialTrace) {
        for (TestCase testCase : partialTrace.getHitSpectrum().keySet()) {
            HitVector hitVector = this.hitSpectrum.getHitSpectrum().get(testCase);
            Set<Component> filteredComponents = partialTrace.get(testCase).getFilteredComponents();
            hitVector.filterComponentsGivenWhichToRemove(filteredComponents);
        }
        this.filteredTracesFlag = true;
    }

    @Override
    public void execute(boolean multipleFaults) {
        this.execute(multipleFaults, null);
    }

    public void execute(boolean multipleFaults, TestCase testCase) {
        if (!filteredTracesFlag) {
            filterTraces();
        }
        if (testCase != null) {
            setTestCase(testCase);
        }
        reconstructTraces();
        super.execute(multipleFaults);
    }

    protected void setTestCase(TestCase testCase) {
        HitVector hitVector = hitSpectrum.get(testCase);
        hitSpectrum.getHitSpectrum().clear();
        hitSpectrum.add(testCase, hitVector);
    }

    protected void filterTraces() {
        logger.info("Filtering traces");
        hitSpectrum.filterComponents(this.filterStrategy);
    }

    protected void reconstructTraces() {
        logger.info("Reconstructing traces");
        try {
            reconstructionApproach.setFullTraceDebug(fullTrace);
            hitSpectrum.reconstructTraces(reconstructionApproach);
        } catch (ReconstructionException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public ExecutionGraph getGraph() {
        return reconstructionApproach.getExecutionGraph();
    }

    @Override
    public void add(String testPackage, String testClass, String testMethod, String path, boolean failed) {
        TestCase testCase = new TestCase(testPackage, testClass, testMethod, failed);
        HitVector hitVector = new ExecutionsParser().parse(path);
        hitSpectrum.add(testCase, hitVector);
        fullTrace.add(testCase, hitVector.clone());
    }

    public Map<TestCase, Triple<Boolean, Boolean, Float>> queryExecution(GroundTruth groundTruth) {
        filterTraces();
        try {
            return hitSpectrum.queryReconstructTraces(reconstructionApproach, groundTruth);
        } catch (ReconstructionException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;


    }

    public HitSpectrum getFullTrace() {
        return fullTrace;
    }

    public ReconstructionApproach getReconstructionApproach() {
        return reconstructionApproach;
    }
}
