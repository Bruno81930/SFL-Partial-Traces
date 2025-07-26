package org.diagnosis.app.cli.queries;

import org.diagnosis.app.exceptions.queries.GroundTruthInExceptionGraphException;
import org.diagnosis.app.model.queries.GroundTruthInExecutionGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.nio.file.Paths;

@CommandLine.Command(name = "ground-truth", description = "Checks if the ground truth is in the execution path", mixinStandardHelpOptions = true, version = "evaluate 1.0", sortOptions = false)
public class GroundTruthInExecutionGraphCli implements Runnable {
    @CommandLine.Option(names={"-p", "--project"}, order = 1, required=true, description="Project name")
    String project;

    @CommandLine.Option(names={"-b", "--bug"}, order = 2, required=true, description="Bug identifier")
    String bug;

    @CommandLine.Option(names={"-P", "--percentage"}, order = 3, description="Percentage of components in the filtered partial trace")
    String percentage;

    @CommandLine.Option(names={"-d", "--data"}, order = 4, required=true, description="Data path")
    String data;

    @CommandLine.Option(names={"-s", "--source"}, order = 5, description="Sources path")
    String[] sources;

    @CommandLine.Option(names={"-l", "--local"}, order = 6, description="Local execution")
    boolean local;

    Logger logger = LoggerFactory.getLogger(GroundTruthInExecutionGraphCli.class);

    @Override
    public void run() {
        System.out.println("Project: " + project);
        System.out.println("Bug: " + bug);
        System.out.println("Percentage: " + percentage);
        System.out.println("Data: " + data);
        System.out.println("Sources: " + sources);
        System.out.println("Local: " + local);

        System.out.println("Ground truth in execution graph");

        if (!Paths.get(data).toFile().exists() && !Paths.get(data).toFile().isDirectory()) {
            logger.error("Data path does not exist");
            return;
        }

        GroundTruthInExecutionGraph groundTruthInExecutionGraph = new GroundTruthInExecutionGraph(project, bug, percentage, data, sources, local);
        try {
            groundTruthInExecutionGraph.query();
        } catch (GroundTruthInExceptionGraphException e) {
            logger.error("Error evaluating wasted effort", e);
        }

    }
}
