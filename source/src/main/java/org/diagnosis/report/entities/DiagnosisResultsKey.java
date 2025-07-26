package org.diagnosis.report.entities;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class DiagnosisResultsKey implements Serializable {
    private String project;
    private String bug;
    private String algorithm;
    private String filterHeuristic;
    private String filterPercentage;
    private String formula;
    private String iteration;
    private String metric;

    public DiagnosisResultsKey() {
    }

    public DiagnosisResultsKey(String project, String bug, String algorithm, String filterPercentage, String filterHeuristic, String formula, String iteration, String metric) {
        this.project = project;
        this.bug = bug;
        this.algorithm = algorithm;
        this.filterPercentage = filterPercentage;
        this.filterHeuristic = filterHeuristic;
        this.formula = formula;
        this.iteration = iteration;
        this.metric = metric;
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

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getFilterHeuristic() {
        return filterHeuristic;
    }

    public void setFilterHeuristic(String filterHeuristic) {
        this.filterHeuristic = filterHeuristic;
    }

    public String getFilterPercentage() {
        return filterPercentage;
    }

    public void setFilterPercentage(String filterPercentage) {
        this.filterPercentage = filterPercentage;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public void setIteration(String iteration) {
        this.iteration = iteration;
    }

    public String getIteration() {
        return iteration;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DiagnosisResultsKey))
            return false;

        DiagnosisResultsKey that = (DiagnosisResultsKey) o;

        if (!getProject().equals(that.getProject()))
            return false;
        if (!getBug().equals(that.getBug()))
            return false;
        if (!getAlgorithm().equals(that.getAlgorithm()))
            return false;
        if (!getFilterHeuristic().equals(that.getFilterHeuristic()))
            return false;
        if (!getFilterPercentage().equals(that.getFilterPercentage()))
            return false;
        if (!getFormula().equals(that.getFormula()))
            return false;
        if (!getIteration().equals(that.getIteration()))
            return false;
        return getMetric().equals(that.getMetric());
    }

    @Override
    public int hashCode() {
        int result = getProject().hashCode();
        result = 31 * result + getBug().hashCode();
        result = 31 * result + getAlgorithm().hashCode();
        result = 31 * result + getFilterHeuristic().hashCode();
        result = 31 * result + getFilterPercentage().hashCode();
        result = 31 * result + getFormula().hashCode();
        result = 31 * result + getIteration().hashCode();
        result = 31 * result + getMetric().hashCode();
        return result;
    }

}
