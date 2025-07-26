package org.diagnosis.app.model.queries;

import org.apache.commons.lang3.tuple.Triple;
import org.diagnosis.algorithms.Algorithm;
import org.diagnosis.algorithms.ReconstructionAlgorithm;
import org.diagnosis.algorithms.entities.TestCase;
import org.diagnosis.algorithms.filter.FilterStrategy;
import org.diagnosis.algorithms.filter.RandomFilterStrategy;
import org.diagnosis.algorithms.reconstruction.techniques.bruteforce.inference.InferenceStrategy;
import org.diagnosis.app.exceptions.queries.GroundTruthInExceptionGraphException;
import org.diagnosis.app.exceptions.reconstructed.ReconstructedEvaluateException;
import org.diagnosis.evaluation.ground_truth.GroundTruth;
import org.diagnosis.evaluation.ground_truth.GroundTruthException;
import org.diagnosis.report.GroundTruthInExecutionGraphReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class GroundTruthInExecutionGraph implements Query {

    private final String project;
    private final String bug;
    private final float percentage;
    private final String[] sources;
    private final String basePath;
    private final Path repositoryPath;
    private final Path allTestsPath;
    private final Path failedTestsPath;
    private final Path tracesPath;
    private final boolean local;

    Logger logger = LoggerFactory.getLogger(GroundTruthInExecutionGraph.class);

    public GroundTruthInExecutionGraph(String project, String bug, String percentage, String dataPath, String[] sources, boolean local) {
        this.project = project;
        this.bug = bug;
        this.percentage = Float.parseFloat(percentage);
        this.sources = sources;
        this.basePath = Paths.get(dataPath, project, bug).toString();
        this.repositoryPath = Paths.get(basePath, "repo");
        this.allTestsPath = Paths.get(basePath, "all_tests.txt");
        this.failedTestsPath = Paths.get(basePath, "failed_tests.txt");
        this.tracesPath = Paths.get(basePath, "traces");
        this.local = local;
    }

    protected void buildSpectrum(Algorithm algorithm) throws ReconstructedEvaluateException {
        List<String> failedTests = null;
        try {
            failedTests = Files.readAllLines(failedTestsPath);
        } catch (IOException e) {
            throw new ReconstructedEvaluateException("Failed to read failed tests file", e);
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
                algorithm.add(packageName, className, methodName, tracePath, isFailed);
            }
        } catch (IOException e) {
            throw new ReconstructedEvaluateException("Failed to read all tests file", e);
        }
    }

    @Override
    public void query() throws GroundTruthInExceptionGraphException {
        FilterStrategy filterStrategy = new RandomFilterStrategy(percentage);
        InferenceStrategy inferenceStrategy = new InferenceStrategy();
        ReconstructionAlgorithm algorithm = new ReconstructionAlgorithm(filterStrategy, inferenceStrategy, this.repositoryPath.toString(), this.sources);
        try {
            logger.info("Building the spectrum");
            buildSpectrum(algorithm);
        } catch (ReconstructedEvaluateException e) {
            throw new GroundTruthInExceptionGraphException("Failed to build the spectrum", e);
        }

        GroundTruth groundTruth;
        try {
            logger.info("Reading the ground truth");
            groundTruth = GroundTruth.readFile(this.basePath);
        } catch (GroundTruthException e) {
            throw new GroundTruthInExceptionGraphException("Failed to read the ground truth", e);
        }


        Map<TestCase, Triple<Boolean, Boolean, Float>> isPresentInTestMap = algorithm.queryExecution(groundTruth);

        if (!local) {
            for (TestCase test : isPresentInTestMap.keySet()) {
                GroundTruthInExecutionGraphReport report = new GroundTruthInExecutionGraphReport(project, bug, percentage, test.toString(), isPresentInTestMap.get(test).getLeft(), isPresentInTestMap.get(test).getMiddle(), isPresentInTestMap.get(test).getRight());
                report.report();
            }

        }

    }
}
