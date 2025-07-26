package org.diagnosis.app.model;

import org.diagnosis.algorithms.ReconstructionAlgorithm;
import org.diagnosis.algorithms.filter.FilterStrategy;
import org.diagnosis.algorithms.reconstruction.techniques.bruteforce.inference.InferenceStrategy;
import org.diagnosis.app.EvaluateException;
import org.diagnosis.app.cli.Computation;
import org.diagnosis.app.cli.Filter;
import org.diagnosis.app.cli.Walk;
import org.diagnosis.evaluation.ground_truth.GroundTruth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;

public class ReconstructedEvaluate extends Evaluate{

    private final String[] sources;

    Logger logger = LoggerFactory.getLogger(ReconstructedEvaluate.class);

    public ReconstructedEvaluate(String project, String bug, String percentage, String dataPath, String[] sources, Filter filter, int times, int fromIteration, boolean report, boolean local) {
        super(project, bug, percentage, dataPath, filter, Walk.NA, Computation.NA, local, times, fromIteration, report);
        this.sources = sources;
    }

    @Override
    void runEvaluation(int iteration, LocalTime start, GroundTruth groundTruth, FilterStrategy filterStrategy) throws EvaluateException {
        InferenceStrategy inferenceStrategy = new InferenceStrategy();
        ReconstructionAlgorithm algorithm = new ReconstructionAlgorithm(filterStrategy, inferenceStrategy, this.repositoryPath.toString(), this.sources);

        logger.info("Building the spectrum for the iteration {}", iteration);
        this.buildSpectrum(algorithm);

        logger.info("Building the hit spectrum for the iteration {}", iteration);
        algorithm.execute(false);

        boolean faultFiltered = !algorithm.getHitSpectrum().getFilteredComponents().contains(groundTruth.getComponents().get(0));
        computeFormulas(iteration, start, algorithm, "(M) Reconstructed", groundTruth, filterStrategy, faultFiltered);
    }
}
