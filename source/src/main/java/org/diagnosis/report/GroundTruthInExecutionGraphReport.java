package org.diagnosis.report;

import org.diagnosis.report.entities.GroundTruthInExecutionGraphEntity;
import org.diagnosis.report.entities.ReportEntity;

public class GroundTruthInExecutionGraphReport extends QueryReport{

    private final String project;
    private final String bug;
    private final String test;
    private final String percentage;
    private final boolean bugInExecution;
    private final boolean bugInReconstruction;
    private final String percentageOfPathsForBug;

    public GroundTruthInExecutionGraphReport(String project, String bug, Float percentage, String test, boolean bugInExecution, boolean bugInReconstruction, Float percentageOfPathsForBug) {
        this.project = project;
        this.bug = bug;
        this.percentage = percentage.toString();
        this.test = test;
        this.bugInExecution = bugInExecution;
        this.bugInReconstruction = bugInReconstruction;
        this.percentageOfPathsForBug = percentageOfPathsForBug.toString();
    }


    public ReportEntity newEntity() {
        return new GroundTruthInExecutionGraphEntity(
                project,
                bug,
                percentage,
                test,
                bugInExecution,
                bugInReconstruction,
                percentageOfPathsForBug);
    }

    @Override
    public void report() {
        super.report(GroundTruthInExecutionGraphEntity.class);
    }
}
