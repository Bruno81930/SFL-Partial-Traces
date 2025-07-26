package org.diagnosis.reconstruction;

import org.diagnosis.algorithms.entities.HitSpectrum;
import org.diagnosis.algorithms.entities.HitVector;
import org.diagnosis.algorithms.entities.TestCase;
import org.diagnosis.algorithms.filter.RandomFilterStrategy;
import org.diagnosis.algorithms.parser.ExecutionsParser;
import org.diagnosis.algorithms.reconstruction.ReconstructionApproach;
import org.diagnosis.algorithms.reconstruction.ReconstructionException;
import org.diagnosis.algorithms.reconstruction.SourceModel;
import org.diagnosis.algorithms.reconstruction.techniques.bruteforce.inference.InferenceStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class ReconstructionTest {
    @Test
    public void test() throws ReconstructionException {
        float percentageOfComponents = 80.0f;

        HitSpectrum hitSpectrum = new HitSpectrum();
        TestCase testCase = new TestCase("", "BasicJobTest", "testSimpleJob", false);
        HitVector hitVector = (new ExecutionsParser()).parse("src/test/resources/full_project_samples/traces/BasicJobTest_testSimpleJob.xml");
        hitSpectrum.add(testCase, hitVector);

        RandomFilterStrategy randomFilterStrategy = new RandomFilterStrategy(percentageOfComponents);
        hitSpectrum.filterComponents(randomFilterStrategy);
        InferenceStrategy inferenceStrategy = new InferenceStrategy();
        ReconstructionApproach algorithm = new ReconstructionApproach(inferenceStrategy, "src/test/resources/data/openpnp/1/repo", new String[]{"src"}, true, new SourceModel("src/test/resources/data/openpnp/1/repo", new String[]{"src"}));
        hitSpectrum.reconstructTraces(algorithm);
    }

    @Test
    public void test2() throws ReconstructionException {
        float percentageOfComponents = 30.0f;

        HitSpectrum hitSpectrum = new HitSpectrum();
        TestCase testCase = new TestCase("", "SampleJobTest", "testSampleJob", false);
        HitVector hitVector = (new ExecutionsParser()).parse("src/test/resources/full_project_samples/traces/SampleJobTest_testSampleJob.xml");
        hitSpectrum.add(testCase, hitVector);

        RandomFilterStrategy randomFilterStrategy = new RandomFilterStrategy(percentageOfComponents);
        hitSpectrum.filterComponents(randomFilterStrategy);
        InferenceStrategy inferenceStrategy = new InferenceStrategy();
        ReconstructionApproach algorithm = new ReconstructionApproach(inferenceStrategy, "src/test/resources/data/openpnp/1/repo", new String[]{"src"}, true, new SourceModel("src/test/resources/data/openpnp/1/repo", new String[]{"src"}));
        hitSpectrum.reconstructTraces(algorithm);
    }

    @Test
    public void test3() throws ReconstructionException {
        float percentageOfComponents = 30.0f;

        HitSpectrum hitSpectrum = new HitSpectrum();
        TestCase testCase = new TestCase("", "Utils2DTest", "testCalculateAngleAndOffset", false);
        HitVector hitVector = (new ExecutionsParser()).parse("src/test/resources/full_project_samples/traces/Utils2DTest_testCalculateAngleAndOffset.xml");
        hitSpectrum.add(testCase, hitVector);

        RandomFilterStrategy randomFilterStrategy = new RandomFilterStrategy(percentageOfComponents);
        hitSpectrum.filterComponents(randomFilterStrategy);
        InferenceStrategy inferenceStrategy = new InferenceStrategy();
        ReconstructionApproach algorithm = new ReconstructionApproach(inferenceStrategy, "src/test/resources/data/openpnp/1/repo", new String[]{"src"}, true, new SourceModel("src/test/resources/data/openpnp/1/repo", new String[]{"src"}));
        hitSpectrum.reconstructTraces(algorithm);
    }

    public static Stream<Arguments> testCases() {
        return Stream.of(
            Arguments.of("src/test/resources/full_project_samples/traces/BasicJobTest_testSimpleJob.xml", "BasicJobTest", "testSimpleJob", false),
            Arguments.of("src/test/resources/full_project_samples/traces/EagleLoaderTest_testLoadBoard.xml", "EagleLoaderTest", "testLoadBoard", false),
            Arguments.of("src/test/resources/full_project_samples/traces/EagleLoaderTest_testLoadSchematic.xml", "EagleLoaderTest", "testLoadSchematic", false),
            Arguments.of("src/test/resources/full_project_samples/traces/OpenCvTest_openCvWorks.xml", "OpenCvTest", "openCvWorks", false),
            Arguments.of("src/test/resources/full_project_samples/traces/SampleJobTest_testSampleJob.xml", "SampleJobTest", "testSampleJob", true),
            Arguments.of("src/test/resources/full_project_samples/traces/Utils2DTest_testCalculateAngleAndOffset.xml", "Utils2DTest", "testCalculateAngleAndOffset", false),
            Arguments.of("src/test/resources/full_project_samples/traces/Utils2DTest_testCalculateBoardPlacementLocation.xml", "Utils2DTest", "testCalculateBoardPlacementLocation", false),
            Arguments.of("src/test/resources/full_project_samples/traces/VisionUtilsTest_testOffsets.xml", "VisionUtilsTest", "testOffsets", false)
        );
    }
    @ParameterizedTest
    @MethodSource("testCases")
    public void test2(String src, String className, String methodName, boolean status) throws ReconstructionException {
        float percentageOfComponents = 30.0f;

        HitSpectrum hitSpectrum = new HitSpectrum();
        TestCase testCase = new TestCase("", className, methodName, status);
        HitVector hitVector = (new ExecutionsParser()).parse(src);
        hitSpectrum.add(testCase, hitVector);

        RandomFilterStrategy randomFilterStrategy = new RandomFilterStrategy(percentageOfComponents);
        hitSpectrum.filterComponents(randomFilterStrategy);
        InferenceStrategy inferenceStrategy = new InferenceStrategy();
        ReconstructionApproach algorithm = new ReconstructionApproach(inferenceStrategy, "src/test/resources/data/openpnp/1/repo", new String[]{"src"}, true, new SourceModel("src/test/resources/data/openpnp/1/repo", new String[]{"src"}));
        hitSpectrum.reconstructTraces(algorithm);
    }

    @Test
    public void testJxPath() throws ReconstructionException {
        float percentageOfComponents = 30.0f;

        HitSpectrum hitSpectrum = new HitSpectrum();
        TestCase testCase = new TestCase("org.apache.commons.jxpath.ri.compiler", "ContextDependencyTest", "testContextDependency", false);
        HitVector hitVector = (new ExecutionsParser()).parse("src/test/resources/data/jxpath/5/traces/org.apache.commons.jxpath.ri.compiler.ContextDependencyTest_testContextDependency.xml");
        hitSpectrum.add(testCase, hitVector);

        RandomFilterStrategy randomFilterStrategy = new RandomFilterStrategy(percentageOfComponents);
        hitSpectrum.filterComponents(randomFilterStrategy);
        InferenceStrategy inferenceStrategy = new InferenceStrategy();
        ReconstructionApproach algorithm = new ReconstructionApproach(inferenceStrategy, "src/test/resources/data/jxpath/5/repo", new String[]{"src/java", "test/test"}, true, new SourceModel("src/test/resources/data/openpnp/1/repo", new String[]{"src"}));
        hitSpectrum.reconstructTraces(algorithm);
    }

    @Test
    public void testMockito() throws ReconstructionException {
        float percentageOfComponents = 30.0f;

        HitSpectrum hitSpectrum = new HitSpectrum();
        TestCase testCase = new TestCase("org.mockito.internal.creation.bytebuddy", "ByteBuddyMockMakerTest", "should_create_mock_from_class", false);
        HitVector hitVector = (new ExecutionsParser()).parse("src/test/resources/data/mockito/1/traces/org.mockito.internal.creation.bytebuddy.ByteBuddyMockMakerTest_should_create_mock_from_class.xml");
        hitSpectrum.add(testCase, hitVector);

        RandomFilterStrategy randomFilterStrategy = new RandomFilterStrategy(percentageOfComponents);
        hitSpectrum.filterComponents(randomFilterStrategy);
        InferenceStrategy inferenceStrategy = new InferenceStrategy();
        ReconstructionApproach algorithm = new ReconstructionApproach(inferenceStrategy, "src/test/resources/data/mockito/1/repo", new String[]{"src/java", "test/test"}, true, new SourceModel("src/test/resources/data/openpnp/1/repo", new String[]{"src"}));
        hitSpectrum.reconstructTraces(algorithm);
    }

    @Test
    public void testGson() throws ReconstructionException {
        float percentageOfComponents = 30.0f;

        HitSpectrum hitSpectrum = new HitSpectrum();
        TestCase testCase = new TestCase("com.google.gson.stream", "JsonReaderTest", "testMalformedNumbers", false);
        HitVector hitVector = (new ExecutionsParser()).parse("src/test/resources/data/gson/12/traces/com.google.gson.stream.JsonReaderTest_testMalformedNumbers.xml");
        hitSpectrum.add(testCase, hitVector);

        RandomFilterStrategy randomFilterStrategy = new RandomFilterStrategy(percentageOfComponents);
        hitSpectrum.filterComponents(randomFilterStrategy);
        InferenceStrategy inferenceStrategy = new InferenceStrategy();
        ReconstructionApproach algorithm = new ReconstructionApproach(inferenceStrategy, "src/test/resources/data/gson/12/repo", new String[]{"src/java", "test/java"}, true, new SourceModel("src/test/resources/data/openpnp/1/repo", new String[]{"src"}));
        hitSpectrum.reconstructTraces(algorithm);
    }

}
