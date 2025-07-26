package org.diagnosis.algorithms.partial_heuristics;

import org.diagnosis.algorithms.AlgorithmException;
import org.diagnosis.algorithms.BaselineAlgorithm;
import org.diagnosis.algorithms.filter.RandomFilterStrategy;
import org.diagnosis.algorithms.reconstruction.techniques.graph.GraphTechnique;
import org.diagnosis.algorithms.sfl.ssfl.Formula;
import org.diagnosis.app.exceptions.partial.PartialEvaluateException;
import org.diagnosis.evaluation.DiagnosisMetrics;
import org.diagnosis.evaluation.ground_truth.GroundTruth;
import org.diagnosis.evaluation.ground_truth.GroundTruthException;
import org.diagnosis.report.DiagnosisReport;
import org.diagnosis.report.PrintReport;
import org.diagnosis.report.Report;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaselineAlgorithmTest {

    @Test
    public void executionsTestSingleFaults() throws AlgorithmException, GroundTruthException {
        float percentageOfComponents = 50.0f;

        BaselineAlgorithm diagnosis = new BaselineAlgorithm(new RandomFilterStrategy(percentageOfComponents));
        diagnosis.add("", "BasicJobTest", "testSimple", "src/test/resources/full_project_samples/traces/BasicJobTest_testSimpleJob.xml", false);
        diagnosis.add("", "EagleLoaderTest", "testLoadBoard", "src/test/resources/full_project_samples/traces/EagleLoaderTest_testLoadBoard.xml", false);
        diagnosis.add("", "EagleLoaderTest", "testLoadSchema", "src/test/resources/full_project_samples/traces/EagleLoaderTest_testLoadSchematic.xml", false);
        diagnosis.add("", "OpenCvTest", "openCvWorks", "src/test/resources/full_project_samples/traces/OpenCvTest_openCvWorks.xml", false);
        diagnosis.add("", "SampleJobTest", "testSampleJob", "src/test/resources/full_project_samples/traces/SampleJobTest_testSampleJob.xml", true);
        diagnosis.add("", "Utils2DTest", "testCalculateAngleAndOffset", "src/test/resources/full_project_samples/traces/Utils2DTest_testCalculateAngleAndOffset.xml", false);
        diagnosis.add("", "Utils2DTest", "testCalculateBoardPlacementLocation", "src/test/resources/full_project_samples/traces/Utils2DTest_testCalculateBoardPlacementLocation.xml", false);
        diagnosis.add("", "VisionUtilsTest", "testOffsets", "src/test/resources/full_project_samples/traces/VisionUtilsTest_testOffsets.xml", false);

        diagnosis.execute(false);

        GroundTruth groundTruth = new GroundTruth();
        groundTruth.addFixedMethod("org.openpnp.machine.reference.ReferenceCamera::new");

        for (Formula formula : Formula.values()) {
            DiagnosisMetrics metrics;
            metrics = new DiagnosisMetrics(diagnosis.getSimilarityCoefficients(formula), groundTruth);
            Report diagnosisReport = new PrintReport(metrics, formula, new RandomFilterStrategy(percentageOfComponents));
            diagnosisReport.report();
        }
    }

}
