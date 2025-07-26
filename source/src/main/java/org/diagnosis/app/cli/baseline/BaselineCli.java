package org.diagnosis.app.cli.baseline;

import org.diagnosis.app.EvaluateException;
import org.diagnosis.app.cli.Filter;
import org.diagnosis.app.exceptions.partial.PartialEvaluateException;
import org.diagnosis.app.model.BaselineEvaluate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.nio.file.Paths;

@Command(name="partial",
        mixinStandardHelpOptions = true,
        description = "Evaluate wasted effort for the partial traces",
        sortOptions = false
)
public class BaselineCli implements Runnable {
    private final static Logger logger = LoggerFactory.getLogger(BaselineCli.class);

    @Option(names={"-p", "--project"}, order = 1, required=true, description="Project name")
    String project;

    @Option(names={"-b", "--bug"}, order = 2, required=true, description="Bug identifier")
    String bug;

    @Option(names={"-P", "--percentage"}, order = 3, description="Percentage of components in the filtered partial trace")
    String percentage;

    @Option(names={"-d", "--data"}, order = 4, required=true, description="Data path")
    String data;

    @Option(names={"-f", "--filter"}, order = 5, required=true, description="Filter strategy [RANDOM|RANDOM_WITH_FAULTS]")
    Filter filter;

    @Option(names={"-t", "--times"}, order = 6, defaultValue = "1", description="Number of times to run the evaluation")
    int times;

    @Option(names={"-i", "--from-iteration"}, order = 7, defaultValue = "0", description="Start iteration")
    int fromIteration;

    @Option(names={"-r", "--report"}, order = 8, description="Report directory path")
    boolean report;

    @Option(names={"-l", "--local"}, order = 9, description = "Local execution")
    boolean local;

    @Override
    public void run() {
        logger.info("Evaluating wasted effort for partial traces");
        logger.info("Project: {}", project);
        logger.info("Bug: {}", bug);
        logger.info("Percentage: {}", percentage);
        logger.info("Data: {}", data);
        logger.info("Filter: {}", filter);
        logger.info("Times: {}", times);
        logger.info("From Iteration: {}", fromIteration);
        logger.info("Report: {}", report);
        logger.info("Local Execution: {}", local);

        if (!Paths.get(data).toFile().exists() && !Paths.get(data).toFile().isDirectory()) {
            logger.error("Data path does not exist");
            return;
        }


        BaselineEvaluate baselineEvaluate = new BaselineEvaluate(project, bug, percentage, data, filter, times, fromIteration, report, local);
        try {
            baselineEvaluate.evaluate();
        } catch (EvaluateException e) {
            logger.error("Error evaluating wasted effort", e);
        }
    }
}

