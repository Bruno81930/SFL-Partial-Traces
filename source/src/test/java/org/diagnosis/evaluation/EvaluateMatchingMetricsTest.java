package org.diagnosis.evaluation;

import org.diagnosis.algorithms.entities.Hit;
import org.diagnosis.algorithms.entities.HitVector;
import org.diagnosis.algorithms.parser.ExecutionsParser;
import org.diagnosis.algorithms.parser.LogsParser;
import org.diagnosis.evaluation.ground_truth.GroundTruth;
import org.diagnosis.evaluation.ground_truth.GroundTruthException;
import org.diagnosis.evaluation.metrics.matching.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EvaluateMatchingMetricsTest {

    private final static Logger logger = LoggerFactory.getLogger(EvaluateMatchingMetricsTest.class);

    @Test
    public void testConfusionMatrix1() {
        HitVector executionTraces = new ExecutionsParser().parse("src/test/resources/full_project_samples/traces/BasicJobTest_testSimpleJob.xml");
        HitVector logTraces = new LogsParser().parse("src/test/resources/full_project_samples/logs/BasicJobTest_testSimpleJob");

        ConfusionMatrix confusionMatrix = new ConfusionMatrix(executionTraces, logTraces) {
            @Override
            public double calculate() {
                return 0;
            }
        };

        logger.info(Integer.toString(confusionMatrix.getTruePositives()));
    }

    @Test
    public void testConfusionMatrix2() {
        HitVector executionsTrace = new HitVector();
        executionsTrace.add(new Hit("A", "A", "A", 1));
        executionsTrace.add(new Hit("B", "B", "B", 1));
        executionsTrace.add(new Hit("C", "C", "C", 0));
        executionsTrace.add(new Hit("D", "D", "D", 0));
        executionsTrace.add(new Hit("E", "E", "E", 1));
        executionsTrace.add(new Hit("F", "F", "F", 1));

        HitVector logsTrace = new HitVector();
        logsTrace.add(new Hit("A", "A", "A", 1));
        logsTrace.add(new Hit("E", "E", "E", 1));
        logsTrace.add(new Hit("F", "F", "F", 1));

        ConfusionMatrix confusionMatrix = new ConfusionMatrix(executionsTrace, logsTrace) {
            @Override
            public double calculate() {
                return 0;
            }
        };

        assertEquals(3, confusionMatrix.getTruePositives());
        assertEquals(0, confusionMatrix.getFalsePositives());
        assertEquals(1, confusionMatrix.getFalseNegatives());
        assertEquals(2, confusionMatrix.getTrueNegatives());
    }

    @Test
    public void testConfusionMatrix3() {
        HitVector executionsTrace = new HitVector();
        executionsTrace.add(new Hit("A", "A", "A", 1));
        executionsTrace.add(new Hit("B", "B", "B", 0));
        executionsTrace.add(new Hit("C", "C", "C", 0));
        executionsTrace.add(new Hit("D", "D", "D", 1));
        executionsTrace.add(new Hit("E", "E", "E", 1));
        executionsTrace.add(new Hit("F", "F", "F", 1));

        HitVector logsTrace = new HitVector();
        logsTrace.add(new Hit("B", "B", "B", 1));
        logsTrace.add(new Hit("C", "C", "C", 1));

        ConfusionMatrix confusionMatrix = new ConfusionMatrix(executionsTrace, logsTrace) {
            @Override
            public double calculate() {
                return 0;
            }
        };

        assertEquals(0, confusionMatrix.getTruePositives());
        assertEquals(2, confusionMatrix.getFalsePositives());
        assertEquals(4, confusionMatrix.getFalseNegatives());
        assertEquals(0, confusionMatrix.getTrueNegatives());
    }

    @Test
    public void testConfusionMatrix4() {
        HitVector executionsTrace = new HitVector();
        executionsTrace.add(new Hit("A", "A", "A", 1));
        executionsTrace.add(new Hit("D", "D", "D", 1));
        executionsTrace.add(new Hit("E", "E", "E", 1));
        executionsTrace.add(new Hit("F", "F", "F", 1));

        HitVector logsTrace = new HitVector();
        logsTrace.add(new Hit("B", "B", "B", 1));
        logsTrace.add(new Hit("C", "C", "C", 1));

        ConfusionMatrix confusionMatrix = new ConfusionMatrix(executionsTrace, logsTrace) {
            @Override
            public double calculate() {
                return 0;
            }
        };

        assertEquals(0, confusionMatrix.getTruePositives());
        assertEquals(2, confusionMatrix.getFalsePositives());
        assertEquals(4, confusionMatrix.getFalseNegatives());
        assertEquals(0, confusionMatrix.getTrueNegatives());
    }

    @Test
    public void testAccuracy1() {
        HitVector executionTraces = new ExecutionsParser().parse("src/test/resources/full_project_samples/traces/BasicJobTest_testSimpleJob.xml");
        HitVector logTraces = new LogsParser().parse("src/test/resources/full_project_samples/logs/BasicJobTest_testSimpleJob");

        Accuracy accuracy = new Accuracy(executionTraces, logTraces);
        assertEquals(0.94, accuracy.calculate(), 0.01);
    }

    @Test
    public void testAccuracy2() {
        HitVector executionsTrace = new HitVector();
        executionsTrace.add(new Hit("A", "A", "A", 1));
        executionsTrace.add(new Hit("B", "B", "B", 1));
        executionsTrace.add(new Hit("C", "C", "C", 0));
        executionsTrace.add(new Hit("D", "D", "D", 0));
        executionsTrace.add(new Hit("E", "E", "E", 1));
        executionsTrace.add(new Hit("F", "F", "F", 1));

        HitVector logsTrace = new HitVector();
        logsTrace.add(new Hit("A", "A", "A", 1));
        logsTrace.add(new Hit("E", "E", "E", 1));
        logsTrace.add(new Hit("F", "F", "F", 1));

        Accuracy accuracy = new Accuracy(executionsTrace, logsTrace);
        assertEquals(0.83, accuracy.calculate(), 0.01);
    }

    @Test
    public void testPrecision() {
        HitVector executionTraces = new ExecutionsParser().parse("src/test/resources/full_project_samples/traces/BasicJobTest_testSimpleJob.xml");
        HitVector logTraces = new LogsParser().parse("src/test/resources/full_project_samples/logs/BasicJobTest_testSimpleJob");

        Precision precision = new Precision(executionTraces, logTraces);
        assertEquals(0.80, precision.calculate(), 0.01);
    }

    @Test
    public void testRecall() {
        HitVector executionTraces = new ExecutionsParser().parse("src/test/resources/full_project_samples/traces/BasicJobTest_testSimpleJob.xml");
        HitVector logTraces = new LogsParser().parse("src/test/resources/full_project_samples/logs/BasicJobTest_testSimpleJob");

        Recall recall = new Recall(executionTraces, logTraces);
        assertEquals(0.08, recall.calculate(), 0.01);
    }

    @Test
    public void testF1Score() {
        HitVector executionTraces = new ExecutionsParser().parse("src/test/resources/full_project_samples/traces/BasicJobTest_testSimpleJob.xml");
        HitVector logTraces = new LogsParser().parse("src/test/resources/full_project_samples/logs/BasicJobTest_testSimpleJob");

        F1Score f1Score = new F1Score(executionTraces, logTraces);
        assertEquals(0.15, f1Score.calculate(), 0.01);
    }

    @Test
    public void testSpecificity() {
        HitVector executionTraces = new ExecutionsParser().parse("src/test/resources/full_project_samples/traces/BasicJobTest_testSimpleJob.xml");
        HitVector logTraces = new LogsParser().parse("src/test/resources/full_project_samples/logs/BasicJobTest_testSimpleJob");

        Specificity specificity = new Specificity(executionTraces, logTraces);
        assertEquals(1.00, specificity.calculate(), 0.01);
    }

    @Test
    public void testFalsePositiveRate() {
        HitVector executionTraces = new ExecutionsParser().parse("src/test/resources/full_project_samples/traces/BasicJobTest_testSimpleJob.xml");
        HitVector logTraces = new LogsParser().parse("src/test/resources/full_project_samples/logs/BasicJobTest_testSimpleJob");

        FalsePositiveRate falsePositiveRate = new FalsePositiveRate(executionTraces, logTraces);
        assertEquals(0.001, falsePositiveRate.calculate(), 0.01);
    }

    @Test
    public void testNegativePredictiveValue() {
        HitVector executionTraces = new ExecutionsParser().parse("src/test/resources/full_project_samples/traces/BasicJobTest_testSimpleJob.xml");
        HitVector logTraces = new LogsParser().parse("src/test/resources/full_project_samples/logs/BasicJobTest_testSimpleJob");

        NegativePredictiveValue negativePredictiveValue = new NegativePredictiveValue(executionTraces, logTraces);
        assertEquals(0.94, negativePredictiveValue.calculate(), 0.01);
    }

    @Test
    public void testFalseDiscoveryRate() {
        HitVector executionTraces = new ExecutionsParser().parse("src/test/resources/full_project_samples/traces/BasicJobTest_testSimpleJob.xml");
        HitVector logTraces = new LogsParser().parse("src/test/resources/full_project_samples/logs/BasicJobTest_testSimpleJob");

        FalseDiscoveryRate falseDiscoveryRate = new FalseDiscoveryRate(executionTraces, logTraces);
        assertEquals(0.20, falseDiscoveryRate.calculate(), 0.01);
    }

    @Test
    public void testMissRate() {
        HitVector executionTraces = new ExecutionsParser().parse("src/test/resources/full_project_samples/traces/BasicJobTest_testSimpleJob.xml");
        HitVector logTraces = new LogsParser().parse("src/test/resources/full_project_samples/logs/BasicJobTest_testSimpleJob");

        MissRate missRate = new MissRate(executionTraces, logTraces);
        assertEquals(0.92, missRate.calculate(), 0.01);
    }

    @Test
    public void testGroundTruthHit1() throws GroundTruthException {
        HitVector executionTraces = new ExecutionsParser().parse("src/test/resources/full_project_samples/traces/BasicJobTest_testSimpleJob.xml");
        GroundTruth groundTruth = new GroundTruth();
        groundTruth.addFixedMethod("org.openpnp.machine.reference.ReferenceCamera::new");

        GroundTruthHits groundTruthHits = new GroundTruthHits(groundTruth, executionTraces, "Execution");
        assertEquals(0.0, groundTruthHits.calculate());

    }

    @Test
    public void testGroundTruthHit2() throws GroundTruthException {
        HitVector executionTraces = new ExecutionsParser().parse("src/test/resources/full_project_samples/traces/SampleJobTest_testSampleJob.xml");
        GroundTruth groundTruth = new GroundTruth();
        groundTruth.addFixedMethod("org.openpnp.machine.reference.ReferenceCamera::new");

        GroundTruthHits groundTruthHits = new GroundTruthHits(groundTruth, executionTraces, "Execution");
        assertEquals(22.0, groundTruthHits.calculate());
    }

}
