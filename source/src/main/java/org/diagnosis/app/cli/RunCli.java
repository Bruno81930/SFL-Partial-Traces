package org.diagnosis.app.cli;

import org.diagnosis.app.cli.baseline.BaselineCli;
import org.diagnosis.app.cli.combined.ExecutionMatchingCli;
import org.diagnosis.app.cli.combined.MatchingCli;
import org.diagnosis.app.cli.queries.QueryCli;
import org.diagnosis.app.cli.reconstructed.ReconstructedCli;
import org.diagnosis.app.cli.sflplus.SFLPlusCli;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name="run",
        mixinStandardHelpOptions = true,
        version = "evaluate 1.0",
        description = "Evaluate wasted effort",
        subcommands = {SFLPlusCli.class, ReconstructedCli.class, BaselineCli.class, QueryCli.class, MatchingCli.class, ExecutionMatchingCli.class}, sortOptions = false

)
public class RunCli implements Runnable {
    private final static Logger logger = LoggerFactory.getLogger(RunCli.class);

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @Override
    public void run() {
        spec.commandLine().usage(System.out);
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new RunCli()).execute(args);
        System.exit(exitCode);
    }
}

