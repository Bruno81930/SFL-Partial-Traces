package org.diagnosis.app.cli.queries;

import picocli.CommandLine;

@CommandLine.Command(name = "query", description = "Query the algorithm", mixinStandardHelpOptions = true, version = "evaluate 1.0", sortOptions = false, subcommands = { GroundTruthInExecutionGraphCli.class })
public class QueryCli implements Runnable {
    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @Override
    public void run() {
        spec.commandLine().usage(System.out);
    }
}
