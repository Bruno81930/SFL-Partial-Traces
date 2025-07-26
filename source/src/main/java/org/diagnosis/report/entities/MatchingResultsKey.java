package org.diagnosis.report.entities;

import org.diagnosis.evaluation.MatchingMetrics;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class MatchingResultsKey implements Serializable {
    private String project;
    private String bug;
    private String testCase;
    private String algorithm;
    private String filterPercentage;
    private String filterHeuristic;
    private String iteration;


    public MatchingResultsKey() {
    }

    public MatchingResultsKey(String project, String bug, String testCase, String algorithm, String filterPercentage, String filterHeuristic, String iteration) {
        this.project = project;
        this.bug = bug;
        this.testCase = testCase;
        this.algorithm = algorithm;
        this.filterPercentage = filterPercentage;
        this.filterHeuristic = filterHeuristic;
        this.iteration = iteration;
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

    public String getTestCase() {
        return testCase;
    }

    public void setTestCase(String testCase) {
        this.testCase = testCase;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getFilterPercentage() {
        return filterPercentage;
    }

    public void setFilterPercentage(String filterPercentage) {
        this.filterPercentage = filterPercentage;
    }

    public String getFilterHeuristic() {
        return filterHeuristic;
    }

    public void setFilterHeuristic(String filterHeuristic) {
        this.filterHeuristic = filterHeuristic;
    }

    public void setIteration(String iteration) {
        this.iteration = iteration;
    }

    public String getIteration() {
        return iteration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MatchingResultsKey)) return false;
        MatchingResultsKey that = (MatchingResultsKey) o;
        return getProject().equals(that.getProject()) && getBug().equals(that.getBug()) && getTestCase().equals(that.getTestCase()) && getAlgorithm().equals(that.getAlgorithm()) && getFilterHeuristic().equals(that.getFilterHeuristic()) && getFilterPercentage().equals(that.getFilterPercentage()) && getIteration().equals(that.getIteration());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProject(), getBug(), getTestCase(), getAlgorithm(), getFilterHeuristic(), getFilterPercentage(), getIteration());
    }
}
