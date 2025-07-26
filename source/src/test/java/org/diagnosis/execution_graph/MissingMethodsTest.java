package org.diagnosis.execution_graph;

import org.apache.poi.ss.formula.functions.T;
import org.diagnosis.algorithms.Algorithm;
import org.diagnosis.algorithms.entities.HitSpectrum;
import org.diagnosis.algorithms.entities.HitVector;
import org.diagnosis.algorithms.entities.TestCase;
import org.diagnosis.algorithms.parser.ExecutionsParser;
import org.diagnosis.algorithms.reconstruction.SourceModel;
import org.diagnosis.algorithms.reconstruction.SourceModelException;
import org.diagnosis.algorithms.reconstruction.graph.ExecutionGraph;
import org.diagnosis.algorithms.reconstruction.graph.ExecutionGraphBuilder;
import org.diagnosis.app.exceptions.reconstructed.ReconstructedEvaluateException;
import org.junit.jupiter.api.Test;
import spoon.reflect.declaration.CtMethod;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MissingMethodsTest {

    @Test
    public void testC128() throws SourceModelException {
        String project = "cli";
        String bug = "4";
        String packageName = "org.apache.commons.cli.bug";
        String className = "BugCLI13Test";
        String methodName = "testCLI13";
        boolean isFailed = false;

        TestCase testCase = new TestCase(packageName, className, methodName, false);
        Path tracePath = Paths.get("src", "test", "data", project, bug, "traces", String.format(packageName + "." + className + "_" + methodName + ".xml"));
        HitVector hitVector = new ExecutionsParser().parse(tracePath.toString());

        Path repositoryPath = Paths.get("src", "test", "data", project, bug, "repo");
        String[] sources = new String[] { "src/org/apache", "test/org/apache" };

        SourceModel sourceModel = new SourceModel(repositoryPath.toString(), sources);
        CtMethod<?> entryMethod = sourceModel.getEntryMethod(packageName, className, methodName);

        ExecutionGraph graph = ExecutionGraphBuilder.build(entryMethod, hitVector);

    }
}
