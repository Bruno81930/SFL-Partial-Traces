package org.diagnosis.app.model;

import org.diagnosis.algorithms.Algorithm;
import org.diagnosis.algorithms.BaselineAlgorithm;
import org.diagnosis.algorithms.ReconstructionAlgorithm;
import org.diagnosis.algorithms.entities.HitSpectrum;
import org.diagnosis.algorithms.entities.HitVector;
import org.diagnosis.algorithms.entities.TestCase;
import org.diagnosis.algorithms.filter.FilterStrategy;
import org.diagnosis.algorithms.reconstruction.SourceModel;
import org.diagnosis.algorithms.reconstruction.techniques.bruteforce.inference.InferenceStrategy;
import org.diagnosis.app.EvaluateException;
import org.diagnosis.app.cli.Computation;
import org.diagnosis.app.cli.Filter;
import org.diagnosis.app.cli.Walk;
import org.diagnosis.evaluation.MatchingMetrics;
import org.diagnosis.evaluation.ground_truth.GroundTruth;
import org.diagnosis.report.MatchingReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MatchingEvaluate extends Evaluate{

    private final String[] sources;
    SourceModel sourceModel;

    Logger logger = LoggerFactory.getLogger(MatchingEvaluate.class);

    public MatchingEvaluate(String project, String bug, String percentage, String dataPath, String[] sources, Filter filter, int times, int fromIteration, boolean report, boolean local) {
        super(project, bug, percentage, dataPath, filter, Walk.NA, Computation.NA, local, times, fromIteration, report);
        this.sources = sources;
        logger.debug("Creating source model");
        this.sourceModel = new SourceModel(this.repositoryPath.toString(), this.sources);
    }

    @Override
    void runEvaluation(int iteration, LocalTime start, GroundTruth groundTruth, FilterStrategy filterStrategy) throws EvaluateException {
        filterStrategy.setSeed(iteration);
        InferenceStrategy inferenceStrategy = new InferenceStrategy();
        BaselineAlgorithm baselineAlgorithm = new BaselineAlgorithm(filterStrategy);
        ReconstructionAlgorithm reconstructionAlgorithm = new ReconstructionAlgorithm(filterStrategy, inferenceStrategy, this.repositoryPath.toString(), this.sources, this.sourceModel, false);
        ReconstructionAlgorithm reconstructionWeightedAlgorithm = new ReconstructionAlgorithm(filterStrategy, inferenceStrategy, this.repositoryPath.toString(), this.sources, this.sourceModel, true);

        List<Algorithm> algorithms = Arrays.asList(baselineAlgorithm, reconstructionAlgorithm, reconstructionWeightedAlgorithm);
        logger.info("Building the spectrum for the iteration {}", iteration);
        this.buildSpectrum(algorithms);

        logger.info("Executing the baseline algorithm for the iteration {}", iteration);
        baselineAlgorithm.execute(false);

        logger.info("Setting the partial trace for reconstruction algorithms for the iteration {}", iteration);
        reconstructionAlgorithm.setPartialTrace(baselineAlgorithm.getHitSpectrum());
        reconstructionWeightedAlgorithm.setPartialTrace(baselineAlgorithm.getHitSpectrum());

        logger.info("Executing the reconstruction algorithms for the iteration {}", iteration);
        reconstructionAlgorithm.execute(false);
        reconstructionWeightedAlgorithm.execute(false);


        //logger.info("Building the hit spectrum for the iteration {}", iteration);
        //algorithms.forEach(algorithm -> algorithm.execute(false));

        logger.info("Computing the matching for the iteration {}", iteration);
        List<String> algorithmsNames = Arrays.asList("Baseline", "Reconstruction", "ReconstructionWeighted");
        computeMatching(iteration, algorithmsNames, algorithms, reconstructionAlgorithm.getFullTrace(), groundTruth, filterStrategy);
    }

    void computeMatching(int iteration, List<String> algorithmsNames, List<Algorithm> algorithms, HitSpectrum fullTraceSpectrum, GroundTruth groundTruth, FilterStrategy filterStrategy) {
        logger.info("Locked the report for the iteration {}", iteration);
        try {
            List<MatchingReport> reports = algorithmsNames.stream().map(algorithm -> new MatchingReport(project, bug, algorithm, percentage, filterStrategy.getName(), Integer.toString(iteration))).collect(Collectors.toList());
            for (int algorithm_idx = 0; algorithm_idx < algorithms.size(); algorithm_idx++) {
                MatchingReport report = reports.get(algorithm_idx);
                for (TestCase testCase : algorithms.get(1).getHitSpectrum().getTests()) {
                    Algorithm algorithm = algorithms.get(algorithm_idx);
                    MatchingMetrics matchingMetrics = new MatchingMetrics(groundTruth, fullTraceSpectrum.get(testCase), algorithm.getHitSpectrum().get(testCase));
                    report.addMetrics(testCase.toString(), matchingMetrics);
                }
            }
            reportLock.lock();
            reports.forEach(MatchingReport::report);
        } finally {
            logger.info("Unlocked the report for the iteration {}", iteration);
            reportLock.unlock();
        }
    }

}
