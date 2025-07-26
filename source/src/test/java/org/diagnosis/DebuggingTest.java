package org.diagnosis;

import org.diagnosis.algorithms.Algorithm;
import org.diagnosis.algorithms.AlgorithmException;
import org.diagnosis.algorithms.BaselineAlgorithm;
import org.diagnosis.algorithms.ReconstructionAlgorithm;
import org.diagnosis.algorithms.filter.FilterStrategy;
import org.diagnosis.algorithms.filter.RandomWithFaultsFilterStrategy;
import org.diagnosis.algorithms.reconstruction.techniques.bruteforce.inference.InferenceStrategy;
import org.diagnosis.algorithms.sfl.ssfl.Formula;
import org.diagnosis.app.exceptions.reconstructed.ReconstructedEvaluateException;
import org.diagnosis.evaluation.ground_truth.GroundTruth;
import org.diagnosis.evaluation.ground_truth.GroundTruthException;
import org.diagnosis.report.HitSpectrumReport;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class DebuggingTest {

    private final static Float percentageOfComponents = 0.10f;
    private final static String OUTPUT = "src/test/diagnosis_output";
    private final static String PATH = "src/test/resources/data";

    private static Stream<Arguments> getWorseSamples() {
        return Stream.of(
                Arguments.of("cli", "14")
                //Arguments.of("codec", "5"),
                //Arguments.of("jacksoncore", "21"),
                //Arguments.of("jsoup", "46"),
                //Arguments.of("openpnp", "1")
        );
    }

    // go inside file in data/project/bug/ground_truth.txt and extract the content of this file into a string
    private String extractGroundTruth(String project, String bug) throws GroundTruthException {
        Path path = Paths.get(PATH,project, bug, "ground_truth.txt");
        try {
            return Files.readAllLines(path).get(0);
        } catch (IOException e) {
            throw new GroundTruthException("Failed to read ground truth file", e);
        }
    }

    protected void buildSpectrum(Algorithm algorithm, String project, String bug) throws ReconstructedEvaluateException {
        Path failedTestsPath = Paths.get(String.format("%s/%s/%s/failed_tests.txt", PATH, project, bug));
        Path allTestsPath = Paths.get(String.format("%s/%s/%s/all_tests.txt", PATH, project, bug));
        Path tracesPath = Paths.get(String.format("%s/%s/%s/traces", PATH, project, bug));

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

    @ParameterizedTest
    @MethodSource("getWorseSamples")
    public void test(String project, String bug) throws GroundTruthException, ReconstructedEvaluateException, AlgorithmException, IOException {
        GroundTruth groundTruth = new GroundTruth();
        groundTruth.addFixedMethod(extractGroundTruth(project, bug));

        FilterStrategy noFilterStrategy = new RandomWithFaultsFilterStrategy(0.0f, groundTruth);
        FilterStrategy filterStrategy = new RandomWithFaultsFilterStrategy(percentageOfComponents, groundTruth);

        BaselineAlgorithm fullTrace = new BaselineAlgorithm(noFilterStrategy);
        BaselineAlgorithm baseline = new BaselineAlgorithm(filterStrategy);
        ReconstructionAlgorithm reconstruction = new ReconstructionAlgorithm(filterStrategy, new InferenceStrategy(), String.format("%s/%s/%s/repo", PATH, project, bug), new String[]{"src", "test"});
        reconstruction.setExecutionGraphDir(Paths.get(OUTPUT, project, bug, "execution_graphs"));
        reconstruction.setGroundTruth(groundTruth);

        buildSpectrum(fullTrace,project, bug);
        buildSpectrum(baseline,project, bug);
        buildSpectrum(reconstruction,project, bug);

        fullTrace.execute(false);
        baseline.execute(false);
        reconstruction.execute(false);

        Formula formula = Formula.ANDERBERG;
        HitSpectrumReport fullReport = new HitSpectrumReport(fullTrace.getHitSpectrum(), groundTruth, fullTrace.getSimilarityCoefficients(formula), Paths.get(OUTPUT, project, bug, "full.xlsx").toFile());
        fullReport.report();
        HitSpectrumReport baselineReport = new HitSpectrumReport(baseline.getHitSpectrum(), groundTruth, baseline.getSimilarityCoefficients(formula), Paths.get(OUTPUT, project, bug, "baseline.xlsx").toFile());
        baselineReport.report();
        HitSpectrumReport reconstructedReport = new HitSpectrumReport(reconstruction.getHitSpectrum(), groundTruth, reconstruction.getSimilarityCoefficients(formula), Paths.get(OUTPUT, project, bug, "reconstructed.xlsx").toFile());
        reconstructedReport.report();
    }
}
