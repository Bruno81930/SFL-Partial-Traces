package org.diagnosis.report.entities;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class MatchingCountResultsKey implements Serializable {
    private String project;
    private String bug;
    private String filterPercentage;
    private String filterHeuristic;
    private String iteration;
    private String testCase;
    private String componentsSet;
    private String hitType;

    public MatchingCountResultsKey() {
    }

    public MatchingCountResultsKey(String project, String bug, String filterPercentage, String filterHeuristic, String iteration, String testCase, String componentsSet, String hitType) {
        this.project = project;
        this.bug = bug;
        this.filterPercentage = filterPercentage;
        this.filterHeuristic = filterHeuristic;
        this.iteration = iteration;
        this.testCase = testCase;
        this.componentsSet = componentsSet;
        this.hitType = hitType;
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

    public String getTestCase() {
        return testCase;
    }

    public void setTestCase(String testCase) {
        this.testCase = testCase;
    }

    public String getComponentsSet() {
        return componentsSet;
    }

    public void setComponentsSet(String componentsSet) {
        this.componentsSet = componentsSet;
    }

    public String getHitType() {
        return hitType;
    }

    public void setHitType(String hitType) {
        this.hitType = hitType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MatchingCountResultsKey)) return false;
        MatchingCountResultsKey that = (MatchingCountResultsKey) o;
        return getProject().equals(that.getProject()) && getBug().equals(that.getBug()) && getFilterPercentage().equals(that.getFilterPercentage()) && getFilterHeuristic().equals(that.getFilterHeuristic()) && getIteration().equals(that.getIteration()) && getTestCase().equals(that.getTestCase()) && getComponentsSet().equals(that.getComponentsSet()) && getHitType().equals(that.getHitType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProject(), getBug(), getFilterPercentage(), getFilterHeuristic(), getIteration(), getTestCase(), getComponentsSet(), getHitType());
    }
}
