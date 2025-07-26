package org.diagnosis.app.model;

import org.diagnosis.algorithms.BaselineAlgorithm;
import org.diagnosis.algorithms.filter.FilterStrategy;
import org.diagnosis.app.EvaluateException;
import org.diagnosis.app.cli.Computation;
import org.diagnosis.app.cli.Filter;
import org.diagnosis.app.cli.Walk;
import org.diagnosis.evaluation.ground_truth.GroundTruth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;

public class BaselineEvaluate extends Evaluate {

    Logger logger = LoggerFactory.getLogger(BaselineEvaluate.class);


    public BaselineEvaluate(String project, String bug, String percentage, String dataPath, Filter filter, int times, int fromIteration, boolean report, boolean local) {
        super(project, bug, percentage, dataPath, filter, Walk.NA, Computation.NA, local, times, fromIteration, report);
    }

    @Override
    void runEvaluation(int iteration, LocalTime start, GroundTruth groundTruth, FilterStrategy filterStrategy) throws EvaluateException {
        BaselineAlgorithm algorithm = new BaselineAlgorithm(filterStrategy);

        logger.info("Building the spectrum for the iteration {}", iteration);
        this.buildSpectrum(algorithm);

        logger.info("Building the hit spectrum for the iteration {}", iteration);
        algorithm.execute(false);

        boolean faultFiltered = !algorithm.getHitSpectrum().getFilteredComponents().contains(groundTruth.getComponents().get(0));
        computeFormulas(iteration, start, algorithm, "(M) Baseline", groundTruth, filterStrategy, faultFiltered);
    }
}
