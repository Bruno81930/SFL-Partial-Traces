package org.diagnosis.app.model;

import org.diagnosis.algorithms.Algorithm;
import org.diagnosis.algorithms.AlgorithmException;
import org.diagnosis.algorithms.filter.FilterStrategy;
import org.diagnosis.algorithms.filter.RandomFilterStrategy;
import org.diagnosis.algorithms.filter.RandomWithFaultsFilterStrategy;
import org.diagnosis.algorithms.sfl.ssfl.Formula;
import org.diagnosis.algorithms.sfl.ssfl.SimilarityCoefficients;
import org.diagnosis.algorithms.sfl.ssfl.computations.ComputationStrategy;
import org.diagnosis.algorithms.sfl.ssfl.computations.ProbabilitiesComputation;
import org.diagnosis.algorithms.sfl.ssfl.computations.ProbabilitiesHitComputation;
import org.diagnosis.algorithms.walker.InformedRandomWalkStrategy;
import org.diagnosis.algorithms.walker.InformedWalkStrategy;
import org.diagnosis.algorithms.walker.RandomWalkStrategy;
import org.diagnosis.algorithms.walker.WalkStrategy;
import org.diagnosis.algorithms.walker.weightModifiers.SquaredModifier;
import org.diagnosis.app.EvaluateException;
import org.diagnosis.app.cli.Computation;
import org.diagnosis.app.cli.Filter;
import org.diagnosis.app.cli.Walk;
import org.diagnosis.evaluation.DiagnosisMetrics;
import org.diagnosis.evaluation.ground_truth.GroundTruth;
import org.diagnosis.evaluation.ground_truth.GroundTruthException;
import org.diagnosis.report.DiagnosisReport;
import org.diagnosis.report.HitSpectrumReport;
import org.diagnosis.report.PrintReport;
import org.diagnosis.report.Report;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public abstract class Evaluate {
    final String project;
    final String bug;
    final String percentage;
    final String basePath;
    final Path allTestsPath;
    final Path failedTestsPath;
    final Path tracesPath;
    final Filter filter;
    final Walk walkType;
    final Computation computation;
    final Path repositoryPath;
    final boolean local;
    final int times;
    final int fromIteration;
    final boolean report;

    static final Lock reportLock = new ReentrantLock();
    static final Semaphore semaphore = new Semaphore(10);



    Logger logger = LoggerFactory.getLogger(Evaluate.class);

    public Evaluate(String project, String bug, String percentage, String dataPath, Filter filter, Walk walkType, Computation computation, boolean local, int times, int fromIteration, boolean report) {
        this.project = project;
        this.bug = bug;
        this.percentage = percentage;
        this.basePath = Paths.get(dataPath, project, bug).toString();
        this.allTestsPath = Paths.get(basePath, "all_tests.txt");
        this.failedTestsPath = Paths.get(basePath, "failed_tests.txt");
        this.tracesPath = Paths.get(basePath, "traces");
        this.repositoryPath = Paths.get(basePath, "repo");
        this.filter = filter;
        this.walkType = walkType;
        this.computation = computation;
        this.local = local;
        this.times = times;
        this.fromIteration = fromIteration;
        this.report = report;
    }

    void buildSpectrum(Algorithm algorithm) throws EvaluateException {
        buildSpectrum(Collections.singletonList(algorithm));
    }

    void buildSpectrum(List<Algorithm> algorithms) throws EvaluateException {
        List<String> failedTests = null;
        try {
            failedTests = Files.readAllLines(failedTestsPath);
        } catch (IOException e) {
            throw new EvaluateException("Failed to read failed tests file", e);
        }

        try {
            for (String line : Files.readAllLines(allTestsPath)) {
                boolean isFailed = failedTests.contains(line);
                String[] parts = line.split(" ");
                String packageName = "";
                String className = parts[0];
                String methodName = parts[1];
                String tracePath;
                if (parts[0].contains(".")) {
                    packageName = parts[0].substring(0, parts[0].lastIndexOf("."));
                    parts[0] = parts[0].substring(parts[0].lastIndexOf(".") + 1);
                    className = parts[0];
                    methodName = parts[1];
                    tracePath = Paths.get(tracesPath.toString(), packageName + "." + className + "_" + methodName + ".xml").toString();
                } else {
                    tracePath = Paths.get(tracesPath.toString(), className + "_" + methodName + ".xml").toString();
                }
                for (Algorithm algorithm : algorithms) {
                    algorithm.add(packageName, className, methodName, tracePath, isFailed);
                }
            }
        } catch (IOException e) {
            throw new EvaluateException("Failed to read all tests file", e);
        }
    }



    GroundTruth getGroundTruth() throws EvaluateException {

        try {
            return GroundTruth.readFile(this.basePath);
        } catch (GroundTruthException e) {
            throw new EvaluateException("Failed to read the ground truth", e);
        }
    }

    FilterStrategy getFilterStrategy(int iteration, GroundTruth groundTruth) throws EvaluateException {
        switch (this.filter) {
            case RANDOM_WITH_FAULTS:
                return new RandomWithFaultsFilterStrategy(Float.parseFloat(percentage), iteration, groundTruth);
            case RANDOM:
                return new RandomFilterStrategy(Float.parseFloat(percentage), iteration);
            default:
                throw new EvaluateException("Invalid filter type");
        }
    }

    WalkStrategy getWalkStrategy() throws EvaluateException {
        switch(this.walkType) {
            case RANDOM:
                return new RandomWalkStrategy();
            case RANDOM_INFORMED:
                return new InformedRandomWalkStrategy();
            case RANDOM_INFORMED_SQUARED:
                return new InformedRandomWalkStrategy(new SquaredModifier());
            case INFORMED:
                return new InformedWalkStrategy();
            case NA:
                throw new EvaluateException("Walk type not relevant for the algorithm");
            default:
                throw new EvaluateException("Invalid walk type");
        }
    }

    ComputationStrategy getComputationStrategy() throws EvaluateException {
        switch(this.computation) {
            case PROBABILITIES:
                return new ProbabilitiesComputation();
            case PROBABILITIES_HIT:
                return new ProbabilitiesHitComputation();
            case NA:
                throw new EvaluateException("Computation type not relevant for the algorithm");
            default:
                throw new EvaluateException("Invalid computation type");
        }
    }

    void createReport(Formula formula, Algorithm algorithm, SimilarityCoefficients coefficients, GroundTruth groundTruth, String output) throws EvaluateException {
        Path formulaReportDirectory = Paths.get("reports", project, bug, percentage, formula.getFormula().getName());
        try {
            Files.createDirectories(formulaReportDirectory);
        } catch (FileAlreadyExistsException e) {
            // ignore this exception
        } catch (IOException e) {
            throw new EvaluateException("Failed to create the report directory", e);
        }

        logger.info("Creating the hit spectrum report for the formula {}", formula.getFormula().getName());
        HitSpectrumReport fullReport = new HitSpectrumReport(algorithm.getHitSpectrum(), groundTruth, coefficients, Paths.get(formulaReportDirectory.toString(), output + ".xlsx").toFile());
        fullReport.report();
    }

    public void evaluate() throws EvaluateException {
        GroundTruth groundTruth = this.getGroundTruth();
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        try {
            for (int iteration = fromIteration; iteration < times; iteration++) {
                int finalIteration = iteration;
                executorService.submit(() -> {
                    try {
                        FilterStrategy filterStrategy = this.getFilterStrategy(finalIteration, groundTruth);
                        runEvaluation(finalIteration, LocalTime.now(), groundTruth, filterStrategy);
                        logger.error("Succeeded evaluating the iteration {}", finalIteration);
                    } catch (EvaluateException | RuntimeException e) {
                        logger.error("Failed to evaluate the iteration {}", finalIteration, e);
                    }
                });
            }
        } catch (Exception e) {
            throw new EvaluateException("Failed to submit the evaluation tasks", e);
        }
        finally {
            shutdownExecutorService(executorService);
        }
    }

    abstract void runEvaluation(int iteration, LocalTime start, GroundTruth groundTruth, FilterStrategy filterStrategy) throws EvaluateException;

    void computeFormulas(int iteration, LocalTime start, Algorithm algorithm, String reportLabel, GroundTruth groundTruth, FilterStrategy filterStrategy, boolean faultFiltered) {
        for (Formula formula : Formula.values()) {
            DiagnosisMetrics metrics;
            SimilarityCoefficients coefficients = null;
            try {
                coefficients = algorithm.getSimilarityCoefficients(formula);
            } catch (AlgorithmException e) {
                throw new RuntimeException("Failed to compute the similarity coefficients", e);
            }

            logger.info("Computing the metrics for the formula {}", formula.getFormula().getName());
            metrics = new DiagnosisMetrics(coefficients, groundTruth);

            Report diagnosisReport;
            if (!local) {
                long durationInMinutes = Duration.between(start, LocalTime.now()).toMinutes();
                logger.info("Sending the metrics to the database");
                diagnosisReport = new DiagnosisReport(project, bug, reportLabel, percentage, filterStrategy.getName(), formula.getFormula().getName(), Integer.toString(iteration + 1), faultFiltered, metrics, durationInMinutes);
            } else {
                logger.info("Printing metrics to the console");
                diagnosisReport = new PrintReport(metrics, formula, filterStrategy);
            }

            reportLock.lock();
            try {
                diagnosisReport.report();
            } finally {
                reportLock.unlock();
            }

            if (report) {
                try {
                    this.createReport(formula, algorithm, coefficients, groundTruth, reportLabel);
                } catch (EvaluateException e) {
                    throw new RuntimeException("Failed to create the report", e);
                }
            }
        }
    }

    void shutdownExecutorService(ExecutorService executorService) {
        executorService.shutdown();
        try {
            // Wait indefinitely for existing tasks to terminate
            while (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
                logger.info("Waiting for the executor service to terminate...");
            }
        } catch (InterruptedException ie) {
            // Preserve interrupt status
            Thread.currentThread().interrupt();
            executorService.shutdownNow();
        }
    }

}
