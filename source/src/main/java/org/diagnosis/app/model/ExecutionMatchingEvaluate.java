package org.diagnosis.app.model;

import org.diagnosis.algorithms.Algorithm;
import org.diagnosis.algorithms.BaselineAlgorithm;
import org.diagnosis.algorithms.ReconstructionAlgorithm;
import org.diagnosis.algorithms.entities.Component;
import org.diagnosis.algorithms.entities.HitSpectrum;
import org.diagnosis.algorithms.entities.HitVector;
import org.diagnosis.algorithms.entities.TestCase;
import org.diagnosis.algorithms.filter.FilterStrategy;
import org.diagnosis.algorithms.reconstruction.ReconstructionApproach;
import org.diagnosis.algorithms.reconstruction.ReconstructionException;
import org.diagnosis.algorithms.reconstruction.SourceModel;
import org.diagnosis.algorithms.reconstruction.graph.ExecutionGraph;
import org.diagnosis.algorithms.reconstruction.techniques.bruteforce.inference.InferenceStrategy;
import org.diagnosis.app.EvaluateException;
import org.diagnosis.app.cli.Computation;
import org.diagnosis.app.cli.Filter;
import org.diagnosis.app.cli.Walk;
import org.diagnosis.evaluation.FullMatchingMetrics;
import org.diagnosis.evaluation.MatchingMetrics;
import org.diagnosis.evaluation.ground_truth.GroundTruth;
import org.diagnosis.evaluation.metrics.full_matching_metrics.AlgorithmType;
import org.diagnosis.report.FullMatchingReport;
import org.diagnosis.report.MatchingReport;
import org.hibernate.HibernateException;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class ExecutionMatchingEvaluate extends Evaluate{

    private final String[] sources;
    SourceModel sourceModel;

    Logger logger = LoggerFactory.getLogger(ExecutionMatchingEvaluate.class);

    public ExecutionMatchingEvaluate(String project, String bug, String percentage, String dataPath, String[] sources, Filter filter, int times, int fromIteration, boolean report, boolean local) {
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
        ReconstructionApproach reconstructionApproach = new ReconstructionApproach(inferenceStrategy, this.repositoryPath.toString(), sources, false, this.sourceModel);
        ReconstructionApproach weightedReconstructionApproach = new ReconstructionApproach(inferenceStrategy, this.repositoryPath.toString(), sources, true, this.sourceModel);

        logger.info("Building the spectrum for the iteration {}", iteration);
        this.buildSpectrum(baselineAlgorithm);

        logger.info("Executing the baseline algorithm for the iteration {}", iteration);
        baselineAlgorithm.execute(false);

        logger.info("Executing the reconstruction algorithm for the iteration {}", iteration);
        FullMatchingReport baselineReport = new FullMatchingReport(project, bug, "Baseline", percentage, filterStrategy.getName(), Integer.toString(iteration));
        FullMatchingReport reconstructedReport = new FullMatchingReport(project, bug, "Reconstructed", percentage, filterStrategy.getName(), Integer.toString(iteration));
        FullMatchingReport weightedReconstructedReport = new FullMatchingReport(project, bug, "WeightedReconstructed", percentage, filterStrategy.getName(), Integer.toString(iteration));

        HitSpectrum partialTrace = baselineAlgorithm.getHitSpectrum();
        for (TestCase testCase : partialTrace.getHitSpectrum().keySet()) {
            HitVector weightedReconstructedHitVector = partialTrace.get(testCase).clone();
            HitVector reconstructedHitVector = partialTrace.get(testCase).clone();
            assert weightedReconstructedHitVector.equals(reconstructedHitVector);
            try {
                reconstructedHitVector = reconstructionApproach.reconstruct(reconstructedHitVector, testCase);
                weightedReconstructedHitVector = weightedReconstructionApproach.reconstruct(weightedReconstructedHitVector, testCase);
            } catch (ReconstructionException e) {
                logger.error("Error while reconstructing the trace for the test case: " + testCase.toString());
                continue;
            }
            ExecutionGraph executionGraph = weightedReconstructionApproach.getExecutionGraph();
            HitVector fullHitVector = baselineAlgorithm.getFullTrace().get(testCase);
            HitVector baselineHitVector = partialTrace.getHitSpectrum().get(testCase);

            baselineReport.addMetrics(testCase.toString(), new FullMatchingMetrics(executionGraph, AlgorithmType.PartialTrace, fullHitVector, baselineHitVector, reconstructedHitVector, weightedReconstructedHitVector));
            reconstructedReport.addMetrics(testCase.toString(), new FullMatchingMetrics(executionGraph, AlgorithmType.ReconstructedTrace, fullHitVector, baselineHitVector, reconstructedHitVector, weightedReconstructedHitVector));
            weightedReconstructedReport.addMetrics(testCase.toString(), new FullMatchingMetrics(executionGraph, AlgorithmType.WeightedReconstructedTrace, fullHitVector, baselineHitVector, reconstructedHitVector, weightedReconstructedHitVector));
        }
        save_report(baselineReport);
        save_report(reconstructedReport);
        save_report(weightedReconstructedReport);
    }

    private void save_report(FullMatchingReport report) {
        //reportLock.lock();
        int retries = 0;
        int MAX_RETRIES = 10;
        final long RETRY_DELAY_MS = 2000; // 2 seconds

        boolean success = false;

        while (!success && retries < MAX_RETRIES) {
            try {
                // Acquire the semaphore
                semaphore.acquire();

                // Attempt to perform the reporting operation
                report.report();

                // If the operation succeeds, set success to true to exit the loop
                success = true;
            } catch (InterruptedException e) {
                // Handle semaphore interruption
                logger.error("The Semaphore was interrupted: " + e.getMessage());
                semaphore.release();
                Thread.currentThread().interrupt(); // Preserve interrupt status
                break; // Exit the loop

            } catch (HibernateException e) {
                // Handle database connection issues
                if (e.getCause() instanceof ConstraintViolationException) {
                    logger.error("Database constraint violation: " + e.getMessage());
                    break; // Exit the loop
                }
                retries++;
                logger.error("Database connection failed (attempt " + retries + " of " + MAX_RETRIES + "): " + e.getMessage());

                if (retries >= MAX_RETRIES) {
                    logger.error("Max retries reached. Operation failed.");
                    break; // Exit the loop if maximum retries are reached
                }

                try {
                    // Wait before retrying
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ie) {
                    // Handle sleep interruption
                    logger.error("Retry delay interrupted: " + ie.getMessage());
                    Thread.currentThread().interrupt(); // Preserve interrupt status
                    break; // Exit the loop
                }
            } finally {
                // Release the semaphore in the finally block to ensure it's always released
                semaphore.release();
            }
        }
    }
}
