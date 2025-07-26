package org.diagnosis.app.model;

import org.diagnosis.algorithms.SFLPlusAlgorithm;
import org.diagnosis.algorithms.filter.FilterStrategy;
import org.diagnosis.algorithms.sfl.ssfl.computations.ComputationStrategy;
import org.diagnosis.algorithms.walker.WalkStrategy;
import org.diagnosis.app.EvaluateException;
import org.diagnosis.app.cli.Computation;
import org.diagnosis.app.cli.Filter;
import org.diagnosis.app.cli.Walk;
import org.diagnosis.evaluation.ground_truth.GroundTruth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;

public class SFLPlusEvaluate extends Evaluate{

    private final int randomWalk;
    private final String[] sources;

    Logger logger = LoggerFactory.getLogger(SFLPlusEvaluate.class);

    public SFLPlusEvaluate(String project, String bug, String percentage, int randomWalk, Walk walkType, Computation computation, String dataPath, String[] sources, Filter filter, int times, int fromIteration, boolean report, boolean local) {
        super(project, bug, percentage, dataPath, filter, walkType, computation, local, times, fromIteration, report);
        this.randomWalk = randomWalk;
        this.sources = sources;
    }

    @Override
    void runEvaluation(int iteration, LocalTime start, GroundTruth groundTruth, FilterStrategy filterStrategy) throws EvaluateException {
        WalkStrategy walkStrategy = this.getWalkStrategy();
        ComputationStrategy computationStrategy = this.getComputationStrategy();
        SFLPlusAlgorithm algorithm = new SFLPlusAlgorithm(filterStrategy, computationStrategy, repositoryPath.toString(), sources, randomWalk, walkStrategy);

        logger.info("Building the spectrum for the iteration {}", iteration);
        this.buildSpectrum(algorithm);

        logger.info("Building the hit spectrum for the iteration {}", iteration);
        algorithm.execute(false);

        boolean faultFiltered = !algorithm.getHitSpectrum().getFilteredComponents().contains(groundTruth.getComponents().get(0));
        String filterStrategyName = "(M) SFL+ - RW" + walkStrategy.report() + computationStrategy.report() + ":" + randomWalk;
        computeFormulas(iteration, start, algorithm, filterStrategyName, groundTruth, filterStrategy, faultFiltered);
    }
}
