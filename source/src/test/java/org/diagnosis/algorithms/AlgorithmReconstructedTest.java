package org.diagnosis.algorithms;

import org.diagnosis.algorithms.filter.FilterStrategy;
import org.diagnosis.algorithms.filter.RandomFilterStrategy;
import org.diagnosis.algorithms.reconstruction.techniques.bruteforce.inference.InferenceStrategy;
import org.junit.jupiter.api.Test;

public class AlgorithmReconstructedTest {



    @Test
    public void test() {
        float percentageOfComponents = 30.0f;

        FilterStrategy filterStrategy = new RandomFilterStrategy(percentageOfComponents);
        InferenceStrategy inferenceStrategy = new InferenceStrategy();
        ReconstructionAlgorithm diagnosis = new ReconstructionAlgorithm(filterStrategy, inferenceStrategy, "src/test/resources/data/openpnp/1/repo", new String[]{"src"});
        diagnosis.add("", "BasicJobTest", "testSimple", "src/test/resources/full_project_samples/traces/BasicJobTest_testSimpleJob.xml", false);
        diagnosis.add("", "EagleLoaderTest", "testLoadBoard", "src/test/resources/full_project_samples/traces/EagleLoaderTest_testLoadBoard.xml", false);
        diagnosis.add("", "EagleLoaderTest", "testLoadSchema", "src/test/resources/full_project_samples/traces/EagleLoaderTest_testLoadSchematic.xml", false);
        diagnosis.add("", "OpenCvTest", "openCvWorks", "src/test/resources/full_project_samples/traces/OpenCvTest_openCvWorks.xml", false);
        diagnosis.add("", "SampleJobTest", "testSampleJob", "src/test/resources/full_project_samples/traces/SampleJobTest_testSampleJob.xml", true);
        diagnosis.add("", "Utils2DTest", "testCalculateAngleAndOffset", "src/test/resources/full_project_samples/traces/Utils2DTest_testCalculateAngleAndOffset.xml", false);
        diagnosis.add("", "Utils2DTest", "testCalculateBoardPlacementLocation", "src/test/resources/full_project_samples/traces/Utils2DTest_testCalculateBoardPlacementLocation.xml", false);
        diagnosis.add("", "VisionUtilsTest", "testOffsets", "src/test/resources/full_project_samples/traces/VisionUtilsTest_testOffsets.xml", false);

    }

}
