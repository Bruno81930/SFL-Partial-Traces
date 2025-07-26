package org.diagnosis.reporter;

import org.diagnosis.algorithms.entities.HitVector;
import org.diagnosis.algorithms.parser.ExecutionsParser;
import org.diagnosis.algorithms.parser.LogsParser;
import org.diagnosis.evaluation.MatchingMetrics;
import org.diagnosis.evaluation.ground_truth.GroundTruth;
import org.diagnosis.evaluation.ground_truth.GroundTruthException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class MatchingReportTest {

    @Test
    @Disabled("Changed the reporting package implementation.")
    public void test() throws GroundTruthException {
        HitVector executionTraces = new ExecutionsParser().parse("src/test/resources/full_project_samples/traces/BasicJobTest_testSimpleJob.xml");
        HitVector logTraces = new LogsParser().parse("src/test/resources/full_project_samples/logs/BasicJobTest_testSimpleJob");
        GroundTruth groundTruth = new GroundTruth();
        groundTruth.addFixedMethod("org.openpnp.machine.reference.ReferenceCamera::new");

        MatchingMetrics matchingMetrics = new MatchingMetrics(groundTruth, executionTraces, logTraces);
    }

    @Test
    @Disabled("Changed the reporting package implementation.")
    public void test2() throws GroundTruthException {
        HitVector executionTraces = new ExecutionsParser().parse("src/test/resources/full_project_samples/traces/SampleJobTest_testSampleJob.xml");
        HitVector logTraces = new LogsParser().parse("src/test/resources/full_project_samples/logs/SampleJobTest_testSampleJob");
        GroundTruth groundTruth = new GroundTruth();
        groundTruth.addFixedMethod("org.openpnp.machine.reference.ReferenceCamera::new");

        MatchingMetrics matchingMetrics = new MatchingMetrics(groundTruth, executionTraces, logTraces);
    }

}
