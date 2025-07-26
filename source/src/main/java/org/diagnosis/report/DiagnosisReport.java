package org.diagnosis.report;

import org.diagnosis.evaluation.DiagnosisMetrics;
import org.diagnosis.evaluation.Metric;
import org.diagnosis.evaluation.Metrics;
import org.diagnosis.report.entities.DiagnosisResultsEntity;
import org.diagnosis.report.entities.ReportEntity;

import java.time.LocalTime;

public class DiagnosisReport extends ResultsReport{

    private final String project;
    private final String bug;
    private final String algorithm;
    private final String filterPercentage;
    private final String filterHeuristic;
    private final String formula;
    private final String iteration;
    private final boolean faultFiltered;
    private final DiagnosisMetrics diagnosisMetrics;
    private final long durationInMinutes;

    public DiagnosisReport(String project, String bug, String algorithm, String filterPercentage, String filterHeuristic, String formula, String iteration, Boolean faultFiltered, DiagnosisMetrics diagnosisMetrics, long durationInMinutes) {
        this.project = project;
        this.bug = bug;
        this.algorithm = algorithm;
        this.filterPercentage = filterPercentage;
        this.filterHeuristic = filterHeuristic;
        this.formula = formula;
        this.iteration = iteration;
        this.faultFiltered = faultFiltered;
        this.diagnosisMetrics = diagnosisMetrics;
        this.durationInMinutes = durationInMinutes;
    }


    @Override
    public void calculateMetrics() {
        this.diagnosisMetrics.calculateAll();
    }

    @Override
    public ReportEntity newEntity(Metric metric) {
        return new DiagnosisResultsEntity(
                project,
                bug,
                algorithm,
                filterPercentage,
                filterHeuristic,
                formula,
                iteration,
                faultFiltered,
                metric.getName(),
                metric.getValue(),
                durationInMinutes);
    }

    @Override
    public Metrics getMetrics() {
        return diagnosisMetrics;
    }

    public void report() {
        super.report(DiagnosisResultsEntity.class);
    }
}
