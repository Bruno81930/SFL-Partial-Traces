package org.diagnosis;

import org.diagnosis.app.exceptions.queries.GroundTruthInExceptionGraphException;
import org.diagnosis.app.model.queries.GroundTruthInExecutionGraph;
import org.junit.jupiter.api.Test;

public class TestGroundTruthQuery {
    @Test
    public void test() throws GroundTruthInExceptionGraphException {
        String project = "cli";
        String bug = "5";
        String percentage = "90";
        String data = "src/test/resources/data";
        String[] sources = new String[]{"src", "test"};
        boolean local = false;

        GroundTruthInExecutionGraph groundTruthInExecutionGraph = new GroundTruthInExecutionGraph(project, bug, percentage, data, sources, local);
        groundTruthInExecutionGraph.query();
    }
}
