package org.diagnosis;

import org.diagnosis.algorithms.*;
import org.diagnosis.algorithms.entities.*;
import org.diagnosis.algorithms.filter.FilterStrategy;
import org.diagnosis.algorithms.filter.RandomFilterStrategy;
import org.diagnosis.algorithms.filter.RandomWithFaultsFilterStrategy;
import org.diagnosis.algorithms.reconstruction.ReconstructionApproach;
import org.diagnosis.algorithms.reconstruction.graph.ExecutionGraph;
import org.diagnosis.algorithms.reconstruction.graph.nodes.Node;
import org.diagnosis.algorithms.reconstruction.techniques.bruteforce.inference.InferenceStrategy;
import org.diagnosis.algorithms.sfl.ssfl.Formula;
import org.diagnosis.algorithms.sfl.ssfl.computations.ComputationStrategy;
import org.diagnosis.algorithms.sfl.ssfl.computations.ProbabilitiesComputation;
import org.diagnosis.algorithms.walker.InformedRandomWalkStrategy;
import org.diagnosis.algorithms.walker.InformedWalkStrategy;
import org.diagnosis.algorithms.walker.RandomWalkStrategy;
import org.diagnosis.algorithms.walker.WalkStrategy;
import org.diagnosis.app.EvaluateException;
import org.diagnosis.app.cli.Filter;
import org.diagnosis.app.exceptions.reconstructed.ReconstructedEvaluateException;
import org.diagnosis.app.model.ExecutionMatchingEvaluate;
import org.diagnosis.evaluation.DiagnosisMetrics;
import org.diagnosis.evaluation.MatchingCountMetrics;
import org.diagnosis.evaluation.MatchingMetrics;
import org.diagnosis.evaluation.ground_truth.GroundTruth;
import org.diagnosis.evaluation.ground_truth.GroundTruthException;
import org.diagnosis.report.MatchingCountReport;
import org.diagnosis.report.MatchingReport;
import org.diagnosis.report.entities.MatchingCountResultsEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class End2EndTest {

    protected void buildSpectrum(Algorithm algorithm, Path failedTestsPath, Path allTestsPath, Path tracesPath) throws ReconstructedEvaluateException {
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

    void fill(String project, String bug, Algorithm algorithm) throws ReconstructedEvaluateException {
        buildSpectrum(algorithm, Paths.get(String.format("src/test/data/%s/%s/failed_tests.txt", project, bug)), Paths.get(String.format("src/test/data/%s/%s/all_tests.txt", project, bug)), Paths.get(String.format("src/test/data/%s/%s/traces", project, bug)));
    }

  @Test
  public void test() throws AlgorithmException, GroundTruthException, ReconstructedEvaluateException, IOException {
      String project = "cli";
      String bug = "4";
      float percentageOfComponents = 50f;
      TestCase testCase = new TestCase("org.apache.commons.cli.bug.BugCLI13Test.testCLI13", false);

      Path out_dir = Paths.get("hits_history", project, bug, testCase.toString());
      Files.createDirectories(out_dir);

      FilterStrategy filterStrategy = new RandomFilterStrategy(percentageOfComponents, 0);
      BaselineAlgorithm baseline = new BaselineAlgorithm(filterStrategy);
      fill(project, bug, baseline);

      ReconstructionAlgorithm reconstruction = new ReconstructionAlgorithm(filterStrategy, new InferenceStrategy(), String.format("src/test/data/%s/%s/repo", project, bug), new String[]{"src/org", "test/org"});
      ReconstructionAlgorithm weighted_reconstruction = new ReconstructionAlgorithm(filterStrategy, new InferenceStrategy(), String.format("src/test/data/%s/%s/repo", project, bug), new String[]{"src/org", "test/org"}, true);
      fill(project, bug, reconstruction);
      fill(project, bug, weighted_reconstruction);

      baseline.execute(false);
      //baseline.execute(false, testCase);

      reconstruction.setPartialTrace(baseline.getHitSpectrum());
      weighted_reconstruction.setPartialTrace(baseline.getHitSpectrum());

      //reconstruction.execute(false);
      reconstruction.execute(false, testCase);
      //weighted_reconstruction.execute(false);
      weighted_reconstruction.execute(false, testCase);

      HitVector fullHitVector = reconstruction.getFullTrace().get(testCase);
      HitVector baselineHitVector = baseline.getHitSpectrum().get(testCase);
      HitVector reconstructionHitVector = reconstruction.getHitSpectrum().get(testCase);
      HitVector weightedReconstructionHitVector = weighted_reconstruction.getHitSpectrum().get(testCase);

      List<Component> components = new ArrayList<>(fullHitVector.getComponents());
      List<Component> componentsInExecutionTrace = weighted_reconstruction.getGraph().filterComponentsInTrace(components);

      FileWriter componentsWriter = new FileWriter(Paths.get(out_dir.toString(), "components.txt").toString());
      componentsWriter.write("id,component\n");
      int counter = 0;
      for (Component component : components) {
          componentsWriter.write("c" + Integer.toString(counter) + "," + component.toString() + "\n");
          counter += 1;
      }
      componentsWriter.flush();
      componentsWriter.close();

      FileWriter hitsWriter = new FileWriter(Paths.get(out_dir.toString(), "traces.csv").toString());
      List<String> componentsIds = components.stream().map(c -> "c" + Integer.toString(components.indexOf(c))).collect(Collectors.toList());
      hitsWriter.write("Algorithm," + String.join(",", componentsIds) + "\n");
      hitsWriter.write("Full," + components.stream().map(fullHitVector::getHit).map(Hit::getNumberOfHits).map(k -> k > 1 ? 1 : k).map(Object::toString).collect(Collectors.joining(",")) + "\n");
      hitsWriter.write("FullInExecutionTrace," + components.stream().map(fullHitVector::getHit).map(h -> componentsInExecutionTrace.contains(h.getComponent()) ? h.getNumberOfHits() : 0).map(k -> k > 1 ? 1 : k).map(Object::toString).collect(Collectors.joining(",")) + "\n");
      hitsWriter.write("Baseline," + components.stream().map(baselineHitVector::getHit).map(Hit::getNumberOfHits).map(k -> k > 1 ? 1 : k).map(Object::toString).collect(Collectors.joining(",")) + "\n");
      hitsWriter.write("Reconstruction," + components.stream().map(reconstructionHitVector::getHit).map(Hit::getNumberOfHits).map(k -> k > 1 ? 1 : k).map(Object::toString).collect(Collectors.joining(",")) + "\n");
      hitsWriter.write("Weighted Reconstruction," + components.stream().map(weightedReconstructionHitVector::getHit).map(Hit::getNumberOfHits).map(k -> k > 1 ? 1 : k).map(Object::toString).collect(Collectors.joining(",")) + "\n");
      hitsWriter.flush();
      hitsWriter.close();

      ReconstructionApproach reconstructionApproach = reconstruction.getReconstructionApproach();
      ReconstructionApproach weightedReconstructionApproach = weighted_reconstruction.getReconstructionApproach();

      Map<Component, String> componentMapper = components.stream().collect(Collectors.toMap(c -> c, c -> "c" + Integer.toString(components.indexOf(c))));
      weightedReconstructionApproach.getExecutionGraph().setFullTrace(fullHitVector);
      weightedReconstructionApproach.getExecutionGraph().setPartialTraces(baselineHitVector);
      String fullGraphString = weightedReconstructionApproach.getExecutionGraph().getFullTraceGraph(componentMapper, true);
      String partialGraphString = weightedReconstructionApproach.getExecutionGraph().getPartialTraceGraph(componentMapper, false);
      String reconstructionComponentString = weightedReconstructionApproach.getExecutionGraph().getReconstructionTraceGraph(componentMapper, reconstructionHitVector);
      String weightedReconstructionComponentString = weightedReconstructionApproach.getExecutionGraph().getReconstructionTraceGraph(componentMapper, weightedReconstructionHitVector);

      FileWriter fullGraphWriter = new FileWriter(Paths.get(out_dir.toString(), "full_graph.dot").toString());
      FileWriter partialGraphWriter = new FileWriter(Paths.get(out_dir.toString(), "partial_graph.dot").toString());
      FileWriter reconstructionWriter = new FileWriter(Paths.get(out_dir.toString(), "reconstruction_graph.dot").toString());
      FileWriter weightedReconstructionWriter = new FileWriter(Paths.get(out_dir.toString(), "weighted_reconstruction_graph.dot").toString());

      fullGraphWriter.write(fullGraphString);
      partialGraphWriter.write(partialGraphString);
      reconstructionWriter.write(reconstructionComponentString);
      weightedReconstructionWriter.write(weightedReconstructionComponentString);

      fullGraphWriter.flush();
      partialGraphWriter.flush();
      reconstructionWriter.flush();
      weightedReconstructionWriter.flush();

      fullGraphWriter.close();
      partialGraphWriter.close();
      reconstructionWriter.close();
      weightedReconstructionWriter.close();

      Set<Component> executionGraphComponents = weighted_reconstruction.getGraph().nodes.values().stream().map(Node::toComponent).collect(Collectors.toSet());
      Set<Component> fullTraceHitComponents = weighted_reconstruction.getFullTrace().getHitSpectrum().get(testCase).getHitComponents();
      Set<Component> fullTraceMissComponents = weighted_reconstruction.getFullTrace().getHitSpectrum().get(testCase).stream().filter(Hit::isMiss).map(Hit::getComponent).collect(Collectors.toSet());
      Set<Component> fullTraceHitInExecutionGraphComponents = executionGraphComponents.stream().filter(fullTraceHitComponents::contains).collect(Collectors.toSet());
      Set<Component> fullTraceMissInExecutionGraphComponents = executionGraphComponents.stream().filter(fullTraceMissComponents::contains).collect(Collectors.toSet());
      Set<Component> baselineFilteredComponents = baseline.getHitSpectrum().get(testCase).getFilteredComponents();
      Set<Component> baselineHitComponents = baseline.getHitSpectrum().get(testCase).getHitComponents();
      Set<Component> baselineMissComponents = baseline.getHitSpectrum().get(testCase).stream().filter(Hit::isMiss).map(Hit::getComponent).collect(Collectors.toSet());
      Set<Component> baselineHitInExecutionGraphComponents = executionGraphComponents.stream().filter(baselineHitComponents::contains).collect(Collectors.toSet());
      Set<Component> baselineMissInExecutionGraphComponents = executionGraphComponents.stream().filter(baselineMissComponents::contains).collect(Collectors.toSet());
      Set<Component> reconstructionHitComponents = reconstruction.getHitSpectrum().get(testCase).getHitComponents();
      Set<Component> reconstructionMissComponents = reconstruction.getHitSpectrum().get(testCase).stream().filter(Hit::isMiss).map(Hit::getComponent).collect(Collectors.toSet());
      Set<Component> reconstructionHitInExecutionGraphComponents = executionGraphComponents.stream().filter(reconstructionHitComponents::contains).collect(Collectors.toSet());
      Set<Component> reconstructionMissInExecutionGraphComponents = executionGraphComponents.stream().filter(reconstructionMissComponents::contains).collect(Collectors.toSet());
      Set<Component> weightedReconstructionHitComponents = weighted_reconstruction.getHitSpectrum().get(testCase).getHitComponents();
      Set<Component> weightedReconstructionMissComponents = weighted_reconstruction.getHitSpectrum().get(testCase).stream().filter(Hit::isMiss).map(Hit::getComponent).collect(Collectors.toSet());
      Set<Component> weightedReconstructionHitInExecutionGraphComponents = executionGraphComponents.stream().filter(weightedReconstructionHitComponents::contains).collect(Collectors.toSet());
      Set<Component> weightedReconstructionMissInExecutionGraphComponents = executionGraphComponents.stream().filter(weightedReconstructionMissComponents::contains).collect(Collectors.toSet());


      FileWriter statsWriter = new FileWriter(Paths.get(out_dir.toString(), "stats.csv").toString());
      statsWriter.write("Algorithm,Type,AllComponents,ExecutionGraphComponents\n");
      statsWriter.write("Components,Type,FullTrace,Baseline,Reconstructed,WeightedReconstructed\n");
      statsWriter.write("All,Hit," + Integer.toString(fullTraceHitComponents.size()) + "," + Integer.toString(baselineHitComponents.size()) + "," + Integer.toString(reconstructionHitComponents.size()) + "," + Integer.toString(weightedReconstructionHitComponents.size()) + "\n");
      statsWriter.write("All,Miss," + Integer.toString(fullTraceMissComponents.size()) + "," + Integer.toString(baselineMissComponents.size()) + "," + Integer.toString(reconstructionMissComponents.size()) + "," + Integer.toString(weightedReconstructionMissComponents.size()) + "\n");
      statsWriter.write("ExecutionGraph,Hit," + Integer.toString(fullTraceHitInExecutionGraphComponents.size()) + "," + Integer.toString(baselineHitInExecutionGraphComponents.size()) + "," + Integer.toString(reconstructionHitInExecutionGraphComponents.size()) + "," + Integer.toString(weightedReconstructionHitInExecutionGraphComponents.size()) + "\n");
      statsWriter.write("ExecutionGraph,Miss," + Integer.toString(fullTraceMissInExecutionGraphComponents.size()) + "," + Integer.toString(baselineMissInExecutionGraphComponents.size()) + "," + Integer.toString(reconstructionMissInExecutionGraphComponents.size()) + "," + Integer.toString(weightedReconstructionMissInExecutionGraphComponents.size()) + "\n");
      statsWriter.flush();
      statsWriter.close();

      List<Component> allComponents = new ArrayList<>(fullHitVector.getComponents());
      Map<Component, String> allComponentMapper = allComponents.stream().collect(Collectors.toMap(c -> c, c -> "c" + Integer.toString(allComponents.indexOf(c))));

      FileWriter componentStatsWriter = new FileWriter(Paths.get(out_dir.toString(), "component_stats.csv").toString());
        componentStatsWriter.write("Component,FullTraceHit,FullTraceMiss,FullTraceHitInExecutionGraph,FullTraceMissInExecutionGraph,BaselineHit,BaselineMiss,BaselineHitInExecutionGraph,BaselineMissInExecutionGraph\n");
      componentStatsWriter.write(allComponents.stream().map(c -> {
          String componentId = allComponentMapper.get(c);
          int fullTraceHit = fullTraceHitComponents.contains(c) ? 1 : 0;
          int fullTraceMiss = fullTraceMissComponents.contains(c) ? 1 : 0;
          int fullTraceHitInExecutionGraph = fullTraceHitInExecutionGraphComponents.contains(c) ? 1 : 0;
          int fullTraceMissInExecutionGraph = fullTraceMissInExecutionGraphComponents.contains(c) ? 1 : 0;
          int baselineHit = baselineHitComponents.contains(c) ? 1 : 0;
          int baselineMiss = baselineMissComponents.contains(c) ? 1 : 0;
          int baselineHitInExecutionGraph = baselineHitInExecutionGraphComponents.contains(c) ? 1 : 0;
          int baselineMissInExecutionGraph = baselineMissInExecutionGraphComponents.contains(c) ? 1 : 0;
          int reconstructionHit = reconstructionHitComponents.contains(c) ? 1 : 0;
          int reconstructionMiss = reconstructionMissComponents.contains(c) ? 1 : 0;
          int reconstructionHitInExecutionGraph = reconstructionHitInExecutionGraphComponents.contains(c) ? 1 : 0;
          int reconstructionMissInExecutionGraph = reconstructionMissInExecutionGraphComponents.contains(c) ? 1 : 0;
          int weightedReconstructionHit = weightedReconstructionHitComponents.contains(c) ? 1 : 0;
          int weightedReconstructionMiss = weightedReconstructionMissComponents.contains(c) ? 1 : 0;
          int weightedReconstructionHitInExecutionGraph = weightedReconstructionHitInExecutionGraphComponents.contains(c) ? 1 : 0;
          int weightedReconstructionMissInExecutionGraph = weightedReconstructionMissInExecutionGraphComponents.contains(c) ? 1 : 0;
          return componentId + "," + fullTraceHit + "," + fullTraceMiss + "," + fullTraceHitInExecutionGraph + "," + fullTraceMissInExecutionGraph + "," + baselineHit + "," + baselineMiss + "," + baselineHitInExecutionGraph + "," + baselineMissInExecutionGraph + "," + reconstructionHit + "," + reconstructionMiss + "," + reconstructionHitInExecutionGraph + "," + reconstructionMissInExecutionGraph + "," + weightedReconstructionHit + "," + weightedReconstructionMiss + "," + weightedReconstructionHitInExecutionGraph + "," + weightedReconstructionMissInExecutionGraph;
      }).collect(Collectors.joining("\n")));

      componentStatsWriter.flush();
      componentStatsWriter.close();

      MatchingCountReport report = new MatchingCountReport("test", "test", "test", "test", "test");
      report.addMetrics("test", new MatchingCountMetrics(weighted_reconstruction.getGraph(), weighted_reconstruction.getFullTrace().getHitSpectrum().get(testCase), baseline.getHitSpectrum().get(testCase), reconstruction.getHitSpectrum().get(testCase), weighted_reconstruction.getHitSpectrum().get(testCase)));
      report.report();
  }

  @Test
  public void testMatchCounting() throws ReconstructedEvaluateException {
      String project = "cli";
      String bug = "4";
      float percentageOfComponents = 50f;

      FilterStrategy filterStrategy = new RandomFilterStrategy(percentageOfComponents, 0);
      BaselineAlgorithm baseline = new BaselineAlgorithm(filterStrategy);
      fill(project, bug, baseline);

      ReconstructionAlgorithm reconstruction = new ReconstructionAlgorithm(filterStrategy, new InferenceStrategy(), String.format("src/test/data/%s/%s/repo", project, bug), new String[]{"src/org", "test/org"});
      ReconstructionAlgorithm weighted_reconstruction = new ReconstructionAlgorithm(filterStrategy, new InferenceStrategy(), String.format("src/test/data/%s/%s/repo", project, bug), new String[]{"src/org", "test/org"}, true);
      fill(project, bug, reconstruction);
      fill(project, bug, weighted_reconstruction);

      baseline.execute(false);

      reconstruction.setPartialTrace(baseline.getHitSpectrum());
      weighted_reconstruction.setPartialTrace(baseline.getHitSpectrum());

      reconstruction.execute(false);
      weighted_reconstruction.execute(false);

      for (TestCase testCase : reconstruction.getFullTrace().getHitSpectrum().keySet()) {
          HitVector fullHitVector = reconstruction.getFullTrace().get(testCase);
          HitVector baselineHitVector = baseline.getHitSpectrum().get(testCase);
          HitVector reconstructionHitVector = reconstruction.getHitSpectrum().get(testCase);
          HitVector weightedReconstructionHitVector = weighted_reconstruction.getHitSpectrum().get(testCase);
          ExecutionGraph executionGraph = weighted_reconstruction.getHitSpectrum().getExecutionGraph(testCase);

          MatchingCountReport report = new MatchingCountReport("cli", "10", Float.toString(percentageOfComponents), "Random", "0");
          report.addMetrics(testCase.toString(), new MatchingCountMetrics(executionGraph, weighted_reconstruction.getFullTrace().getHitSpectrum().get(testCase), baseline.getHitSpectrum().get(testCase), reconstruction.getHitSpectrum().get(testCase), weighted_reconstruction.getHitSpectrum().get(testCase)));
          report.report();
      }
  }

    @Test
    public void testMatchingExecutionGraph() throws ReconstructedEvaluateException, GroundTruthException {
        String project = "cli";
        String bug = "10";
        float percentageOfComponents = 50f;
        GroundTruth groundTruth = new GroundTruth();
        groundTruth.addFixedMethod("org.apache.commons.cli.Parser::setOptions");

        FilterStrategy filterStrategy = new RandomFilterStrategy(percentageOfComponents, 0);
        BaselineAlgorithm baseline = new BaselineAlgorithm(filterStrategy);
        fill(project, bug, baseline);

        ReconstructionAlgorithm reconstruction = new ReconstructionAlgorithm(filterStrategy, new InferenceStrategy(), String.format("src/test/data/%s/%s/repo", project, bug), new String[]{"src/org", "test/org"});
        ReconstructionAlgorithm weighted_reconstruction = new ReconstructionAlgorithm(filterStrategy, new InferenceStrategy(), String.format("src/test/data/%s/%s/repo", project, bug), new String[]{"src/org", "test/org"}, true);
        fill(project, bug, reconstruction);
        fill(project, bug, weighted_reconstruction);

        baseline.execute(false);

        reconstruction.setPartialTrace(baseline.getHitSpectrum());
        weighted_reconstruction.setPartialTrace(baseline.getHitSpectrum());

        reconstruction.execute(false);
        weighted_reconstruction.execute(false);

        MatchingReport baselineReport = new MatchingReport("cliL", "10", "Baseline", Float.toString(percentageOfComponents), "Random", "0");
        MatchingReport reconstructedReport = new MatchingReport("cliL", "10", "Reconstructed", Float.toString(percentageOfComponents), "Random", "0");
        MatchingReport weightedReconstructedReport = new MatchingReport("cliL", "10", "WeightedReconstructed", Float.toString(percentageOfComponents), "Random", "0");

        for (TestCase testCase : baseline.getFullTrace().getHitSpectrum().keySet()) {
            ExecutionGraph executionGraph = weighted_reconstruction.getHitSpectrum().getExecutionGraph(testCase);
            HitVector fullHitVector = baseline.getFullTrace().get(testCase).filterFromExecutionGraph(executionGraph);
            HitVector baselineHitVector = baseline.getHitSpectrum().get(testCase).filterFromExecutionGraph(executionGraph);
            HitVector reconstructionHitVector = reconstruction.getHitSpectrum().get(testCase).filterFromExecutionGraph(executionGraph);
            HitVector weightedReconstructionHitVector = weighted_reconstruction.getHitSpectrum().get(testCase).filterFromExecutionGraph(executionGraph);

            baselineReport.addMetrics(testCase.toString(), new MatchingMetrics(groundTruth, fullHitVector, baselineHitVector));
            reconstructedReport.addMetrics(testCase.toString(), new MatchingMetrics(groundTruth, fullHitVector, reconstructionHitVector));
            weightedReconstructedReport.addMetrics(testCase.toString(), new MatchingMetrics(groundTruth, fullHitVector, weightedReconstructionHitVector));
        }

        baselineReport.report();
        reconstructedReport.report();
        weightedReconstructedReport.report();
    }

    @Test
    public void testEvaluateExecution() throws EvaluateException {
        ExecutionMatchingEvaluate executionMatchingEvaluate = new ExecutionMatchingEvaluate("cli", "10", "50", "src/test/data", new String[]{"src/org", "test/org"}, Filter.RANDOM, 1, 0, false, false);
        executionMatchingEvaluate.evaluate();

    }



    @ParameterizedTest
    @CsvSource(value = {
            "cli,3,org.apache.commons.cli.TypeHandler::createNumber",
            "cli,4,org.apache.commons.cli.Parser::checkRequiredOptions",
            "cli,5,org.apache.commons.cli.Util::stripLeadingHyphens",
            "cli,8,org.apache.commons.cli.HelpFormatter::renderWrappedText",
            "cli,9,org.apache.commons.cli.Parser::checkRequiredOptions",
            "cli,11,org.apache.commons.cli.HelpFormatter::appendOption",
            "cli,12,org.apache.commons.cli.GnuParser::flatten",
            "cli,14,org.apache.commons.cli2.option.GroupImpl::validate"

    }, delimiter = ',')
    public void testMSE(String project, String bug, String groundTruthString) throws AlgorithmException, GroundTruthException, ReconstructedEvaluateException {

        //float percentageOfComponents = 50.0f;
        //String project = "cli";
        //String bug = "3";

        GroundTruth groundTruth = new GroundTruth();
        groundTruth.addFixedMethod(groundTruthString);

        List<Integer> measures = new ArrayList<>();
        List<WalkStrategy> strategies = Arrays.asList(new RandomWalkStrategy(), new InformedWalkStrategy(), new InformedRandomWalkStrategy());

        BaselineAlgorithm baseline = new BaselineAlgorithm(new RandomFilterStrategy(0.0f));
        FilterStrategy filterStrategy = new RandomFilterStrategy(50f);
        ReconstructionAlgorithm reconstruction = new ReconstructionAlgorithm(filterStrategy, new InferenceStrategy(), String.format("src/test/data/%s/%s/repo", project, bug), new String[]{"src/org", "test/org"});

        ComputationStrategy computationStrategy = new ProbabilitiesComputation();
        SFLPlusAlgorithm randomWalk = new SFLPlusAlgorithm(filterStrategy, computationStrategy, String.format("src/test/data/%s/%s/repo", project, bug), new String[]{"src/org", "test/org"}, 1000, new RandomWalkStrategy());
        SFLPlusAlgorithm informedRandomWalk = new SFLPlusAlgorithm(filterStrategy, computationStrategy, String.format("src/test/data/%s/%s/repo", project, bug), new String[]{"src/org", "test/org"}, 1000, new InformedRandomWalkStrategy());
        SFLPlusAlgorithm informedWalk = new SFLPlusAlgorithm(filterStrategy, computationStrategy, String.format("src/test/data/%s/%s/repo", project, bug), new String[]{"src/org", "test/org"}, 1000, new InformedWalkStrategy());

        fill(project, bug, baseline);
        fill(project, bug, reconstruction);
        fill(project, bug, randomWalk);
        fill(project, bug, informedRandomWalk);
        fill(project, bug, informedWalk);

        baseline.execute(false);
        reconstruction.execute(false);
        randomWalk.execute(false);
        informedRandomWalk.execute(false);
        informedWalk.execute(false);

        HitSpectrum full_trace = baseline.getHitSpectrum();
        HitSpectrum reconstruction_trace = reconstruction.getHitSpectrum();
        HitSpectrum random_walk_trace = randomWalk.getHitSpectrum();
        HitSpectrum informed_random_walk_trace = informedRandomWalk.getHitSpectrum();
        HitSpectrum informed_walk_trace = informedWalk.getHitSpectrum();

        System.out.println("Reconstruction trace: " + full_trace.calculateDistanceBetween(reconstruction_trace));
        System.out.println("Random walk trace: " + full_trace.calculateDistanceBetween(random_walk_trace));
        System.out.println("Informed random walk trace: " + full_trace.calculateDistanceBetween(informed_random_walk_trace));
        System.out.println("Informed walk trace: " + full_trace.calculateDistanceBetween(informed_walk_trace));
    }

    @Test
    public void testExtended() throws AlgorithmException, GroundTruthException, ReconstructedEvaluateException {

        float percentageOfComponents = 100.0f;
        String project = "cli";
        String bug = "4";

        GroundTruth groundTruth = new GroundTruth();
        groundTruth.addFixedMethod("org.apache.commons.cli.Parser::checkRequiredOptions");

        FilterStrategy filterStrategy = new RandomWithFaultsFilterStrategy(percentageOfComponents, groundTruth);
        SFLPlusAlgorithm algorithm = new SFLPlusAlgorithm(filterStrategy, String.format("src/test/resources/data/%s/%s/repo", project, bug), new String[]{"src", "test"}, 1000);
        ReconstructionAlgorithm reconstruction = new ReconstructionAlgorithm(filterStrategy, new InferenceStrategy(), String.format("src/test/resources/data/%s/%s/repo", project, bug), new String[]{"src", "test"});
        BaselineAlgorithm baseline = new BaselineAlgorithm(filterStrategy);
        fill(project, bug, algorithm);
        fill(project, bug, reconstruction);
        fill(project, bug, baseline);

        algorithm.execute(false);
        reconstruction.execute(false);
        baseline.execute(false);

        Formula formula = Formula.ANDERBERG;
        DiagnosisMetrics algorithmMetrics = new DiagnosisMetrics(algorithm.getSimilarityCoefficients(formula), groundTruth);
        algorithmMetrics.calculateAll();

        DiagnosisMetrics reconstructionMetrics = new DiagnosisMetrics(reconstruction.getSimilarityCoefficients(formula), groundTruth);
        reconstructionMetrics.calculateAll();

        DiagnosisMetrics baselineMetrics = new DiagnosisMetrics(baseline.getSimilarityCoefficients(formula), groundTruth);
        baselineMetrics.calculateAll();

        System.out.println(algorithmMetrics.get("Wasted Effort"));
    }
}