package org.diagnosis.report.entities;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class GroundTruthInExecutionGraphKey implements Serializable {
    private String project;
    private String bug;
    private String percentage;
    private String failingTest;

    public GroundTruthInExecutionGraphKey() {
    }

    public GroundTruthInExecutionGraphKey(String project, String bug, String percentage, String failingTest) {
        this.project = project;
        this.bug = bug;
        this.percentage = percentage;
        this.failingTest = failingTest;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getBug() {
        return bug;
    }

    public void setBug(String bug) {
        this.bug = bug;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getFailingTest() {
        return failingTest;
    }

    public void setFailingTest(String failingTest) {
        this.failingTest = failingTest;
    }
}
