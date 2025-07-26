package org.diagnosis.report.entities;

import javax.persistence.*;

@Entity
@Table(name="ground_truth_in_execution_graph")
public class GroundTruthInExecutionGraphEntity implements ReportEntity{

    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "project", column = @Column(name = "Project", length = 100)),
            @AttributeOverride(name = "bug", column = @Column(name = "Bug", length = 100)),
            @AttributeOverride(name = "percentage", column = @Column(name = "Percentage", length = 100)),
            @AttributeOverride(name = "failingTest", column = @Column(name = "FailingTest", length = 200))
    })
    private GroundTruthInExecutionGraphKey groundTruthInExecutionGraphKey;

    @Column(name = "BugInExecution")
    private boolean bugInExecution;

    @Column(name = "BugInReconstruction")
    private boolean bugInReconstruction;

    @Column(name = "PercentageOfPathsForBug")
    private String percentageOfPathsForBug;

    public GroundTruthInExecutionGraphEntity() {
    }

    public GroundTruthInExecutionGraphEntity(String project, String bug, String percentage, String failingTest, boolean bugInExecution, boolean bugInReconstruction, String percentageOfPathsForBug) {
        groundTruthInExecutionGraphKey = new GroundTruthInExecutionGraphKey(project, bug, percentage, failingTest);
        this.bugInExecution = bugInExecution;
        this.bugInReconstruction = bugInReconstruction;
        this.percentageOfPathsForBug = percentageOfPathsForBug;
    }

    public boolean isBugInExecution() {
        return bugInExecution;
    }

    public void setBugInExecution(boolean bugInExecution) {
        this.bugInExecution = bugInExecution;
    }

    public boolean isBugInReconstruction() {
        return bugInReconstruction;
    }

    public void setBugInReconstruction(boolean bugInReconstruction) {
        this.bugInReconstruction = bugInReconstruction;
    }

    public String getPercentageOfPathsForBug() {
        return percentageOfPathsForBug;
    }

    public void setPercentageOfPathsForBug(String percentageOfPathsForBug) {
        this.percentageOfPathsForBug = percentageOfPathsForBug;
    }

    public GroundTruthInExecutionGraphKey getGroundTruthInExecutionGraphKey() {
        return groundTruthInExecutionGraphKey;
    }

    public void setGroundTruthInExecutionGraphKey(GroundTruthInExecutionGraphKey groundTruthInExecutionGraphKey) {
        this.groundTruthInExecutionGraphKey = groundTruthInExecutionGraphKey;
    }

    public GroundTruthInExecutionGraphKey getProjectBugKey() {
        return groundTruthInExecutionGraphKey;
    }

    public void setProjectBugKey(GroundTruthInExecutionGraphKey groundTruthInExecutionGraphKey) {
        this.groundTruthInExecutionGraphKey = groundTruthInExecutionGraphKey;
    }
}
