package org.diagnosis.app.cli.sflplus;

import org.diagnosis.app.EvaluateException;
import org.diagnosis.app.cli.Computation;
import org.diagnosis.app.cli.Filter;
import org.diagnosis.app.cli.Walk;
import org.diagnosis.app.exceptions.sflplus.SFLPlusEvaluateException;
import org.diagnosis.app.model.SFLPlusEvaluate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.nio.file.Paths;
import java.util.Arrays;

@Command(name="sfl+",
        mixinStandardHelpOptions = true,
        description = "Evaluate wasted effort using the sfl+ reconstructed traces",
        sortOptions = false
)
public class SFLPlusCli implements Runnable {
    private final static Logger logger = LoggerFactory.getLogger(SFLPlusCli.class);

    @Option(names={"-p", "--project"}, order = 1, required=true, description="Project name")
    String project;

    @Option(names={"-b", "--bug"}, order = 2, required=true, description="Bug identifier")
    String bug;

    @Option(names={"-P", "--percentage"}, order = 3, description="Percentage of components in the filtered partial trace")
    String percentage;

    @Option(names={"-R", "--random-walk"}, order=4, description = "Number of random walks")
    int randomWalk;

    @Option(names={"-W", "--walk-type"}, order=5, description = "Type of random walk [RANDOM|RANDOM_INFORMED|RANDOM_INFORMED_SQUARED|INFORMED]")
    Walk walkType;

    @Option(names={"-C", "--computation"}, order = 6, required=true, description="Computation strategy [PROBABILITIES|PROBABILITIES_HIT]")
    Computation computation;

    @Option(names={"-d", "--data"}, order = 7, required=true, description="Data path")
    String data;

    @Option(names={"-s", "--source"}, order = 8, description="Sources path")
    String[] sources;

    @Option(names={"-f", "--filter"}, order = 9, required=true, description="Filter strategy [RANDOM|RANDOM_WITH_FAULTS]")
    Filter filter;

    @Option(names={"-t", "--times"}, order = 10, defaultValue = "1", description="Number of times to run the evaluation")
    int times;

    @Option(names={"-i", "--from-iteration"}, order = 11, defaultValue = "0", description="Start iteration")
    int fromIteration;

    @Option(names={"-r", "--report"}, order = 11, description="Save diagnosis in reports directory?")
    boolean report;

    @Option(names={"-l", "--local"}, order = 12, description = "Local execution?")
    boolean local;

    @Override
    public void run() {
        logger.info("Evaluating wasted effort for SFL+ reconstructed traces");
        logger.info("Project: {}", project);
        logger.info("Bug: {}", bug);
        logger.info("Percentage: {}", percentage);
        logger.info("Random Walk: {}", randomWalk);
        logger.info("Walk Type: {}", walkType);
        logger.info("Computation: {}", computation);
        logger.info("Data: {}", data);
        logger.info("Sources: {}", Arrays.asList(sources));
        logger.info("Filter: {}", filter);
        logger.info("Times: {}", times);
        logger.info("From Iteration: {}", fromIteration);
        logger.info("Report: {}", report);
        logger.info("Local Execution: {}", local);

        if (!Paths.get(data).toFile().exists() && !Paths.get(data).toFile().isDirectory()) {
            logger.error("Data path does not exist");
            return;
        }


        SFLPlusEvaluate sflPlusEvaluate = new SFLPlusEvaluate(project, bug, percentage, randomWalk, walkType, computation, data, sources, filter, times, fromIteration, report, local);
        try {
            sflPlusEvaluate.evaluate();
        } catch (EvaluateException e) {
            logger.error("Error evaluating wasted effort", e);
        }
    }
}

