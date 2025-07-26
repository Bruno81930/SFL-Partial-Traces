package org.diagnosis.report.entities;

import javax.persistence.*;

@Entity
@Table(name="results")
public class DiagnosisResultsEntity implements ReportEntity {

    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "project", column = @Column(name = "Project", length = 100)),
            @AttributeOverride(name = "bug", column = @Column(name = "Bug", length = 100)),
            @AttributeOverride(name = "algorithm", column = @Column(name = "Algorithm", length = 100)),
            @AttributeOverride(name = "filterPercentage", column = @Column(name = "FilterPercentage", length = 100)),
            @AttributeOverride(name = "filterHeuristic", column = @Column(name = "FilterHeuristic", length = 100)),
            @AttributeOverride(name = "formula", column = @Column(name = "Formula", length = 100)),
            @AttributeOverride(name = "iteration", column = @Column(name = "Iteration", length = 100)),
            @AttributeOverride(name = "metric", column = @Column(name = "Metric", length = 100)),
    })
    private DiagnosisResultsKey diagnosisResultsKey;

    @Column(name = "Value")
    private Double value;

    @Column(name = "DurationInMinutes")
    private long durationInMinutes;

    @Column(name = "FaultFiltered")
    private boolean faultFiltered;

    public DiagnosisResultsEntity() {
    }

    public DiagnosisResultsEntity(String project, String bug, String algorithm, String filterPercentage, String filterHeuristic, String formula, String iteration, boolean faultFiltered, String metric, Double value, long durationInMinutes ) {
        this.diagnosisResultsKey = new DiagnosisResultsKey(project, bug, algorithm, filterPercentage, filterHeuristic, formula, iteration, metric);
        this.value = value;
        this.durationInMinutes = durationInMinutes;
        this.faultFiltered = faultFiltered;
    }

    public DiagnosisResultsKey getResultsKey() {
        return diagnosisResultsKey;
    }

    public void setResultsKey(DiagnosisResultsKey diagnosisResultsKey) {
        this.diagnosisResultsKey = diagnosisResultsKey;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public long getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(long durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public void setFaultFiltered(boolean faultFiltered) {
        this.faultFiltered = faultFiltered;
    }

    public boolean wasFaultFiltered() {
        return faultFiltered;
    }
}
