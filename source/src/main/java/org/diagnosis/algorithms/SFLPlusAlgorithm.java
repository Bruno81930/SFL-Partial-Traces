package org.diagnosis.algorithms;

import org.apache.commons.lang3.tuple.Triple;
import org.diagnosis.algorithms.entities.HitVector;
import org.diagnosis.algorithms.entities.TestCase;
import org.diagnosis.algorithms.filter.FilterStrategy;
import org.diagnosis.algorithms.parser.ExecutionsParser;
import org.diagnosis.algorithms.reconstruction.Reconstruction;
import org.diagnosis.algorithms.reconstruction.ReconstructionApproach;
import org.diagnosis.algorithms.reconstruction.ReconstructionException;
import org.diagnosis.algorithms.reconstruction.SFLPlusApproach;
import org.diagnosis.algorithms.reconstruction.graph.ExecutionGraph;
import org.diagnosis.algorithms.reconstruction.techniques.bruteforce.inference.InferenceStrategy;
import org.diagnosis.algorithms.sfl.ssfl.computations.BinaryComputation;
import org.diagnosis.algorithms.sfl.ssfl.computations.ComputationStrategy;
import org.diagnosis.algorithms.sfl.ssfl.computations.ProbabilitiesComputation;
import org.diagnosis.algorithms.walker.RandomWalkStrategy;
import org.diagnosis.algorithms.walker.WalkStrategy;
import org.diagnosis.evaluation.ground_truth.GroundTruth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class SFLPlusAlgorithm extends Algorithm {

    private final String repositoryPath;
    private final FilterStrategy filterStrategy;
    private final String[] sources;
    private final SFLPlusApproach sflPlusApproach;

    Logger logger = LoggerFactory.getLogger(SFLPlusAlgorithm.class);

    public SFLPlusAlgorithm(FilterStrategy filterStrategy, String repositoryPath, String[] sources, int randomWalkNumber) {
        this(filterStrategy, new ProbabilitiesComputation(), repositoryPath, sources, randomWalkNumber, new RandomWalkStrategy());
    }

    public SFLPlusAlgorithm(FilterStrategy filterStrategy, ComputationStrategy computationStrategy, String repositoryPath, String[] sources, int randomWalkNumber, WalkStrategy walkStrategy) {
        super(computationStrategy);
        this.repositoryPath = repositoryPath;
        this.filterStrategy = filterStrategy;
        this.sources = sources;
        this.sflPlusApproach = new SFLPlusApproach(repositoryPath, sources, randomWalkNumber, walkStrategy);
    }

    @Override
    public void execute(boolean multipleFaults) {
        filterTraces();
        reconstructTraces();
        super.execute(multipleFaults);
    }

    protected void filterTraces() {
        logger.info("Filtering traces");
        hitSpectrum.filterComponents(this.filterStrategy);
    }

    protected void reconstructTraces() {
        logger.info("Reconstructing traces");
        try {
            hitSpectrum.reconstructTraces(sflPlusApproach);
        } catch (ReconstructionException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void add(String testPackage, String testClass, String testMethod, String path, boolean failed) {
        TestCase testCase = new TestCase(testPackage, testClass, testMethod, failed);
        HitVector hitVector = new ExecutionsParser().parse(path);
        hitSpectrum.add(testCase, hitVector);
    }

}
