package org.diagnosis.report.entities;

import org.diagnosis.evaluation.FullMatchingMetrics;
import org.diagnosis.evaluation.MatchingMetrics;

import javax.persistence.*;

@Entity
@Table(name="matching_full_results")
public class FullMatchingResultsEntity implements ReportEntity {

    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "project", column = @Column(name = "Project", length = 100)),
            @AttributeOverride(name = "bug", column = @Column(name = "Bug", length = 100)),
            @AttributeOverride(name = "testCase", column = @Column(name = "TestCase", length = 256)),
            @AttributeOverride(name = "algorithm", column = @Column(name = "Algorithm", length = 100)),
            @AttributeOverride(name = "filterPercentage", column = @Column(name = "FilterPercentage", length = 100)),
            @AttributeOverride(name = "filterHeuristic", column = @Column(name = "FilterHeuristic", length = 100)),
            @AttributeOverride(name = "iteration", column = @Column(name = "Iteration", length = 100))
    })
    private MatchingResultsKey matchingResultsKey;

    @Column(name = "FullF1Score")
    private Double  fullF1Score;

    @Column(name = "FullPrecision")
    private Double  fullPrecision;

    @Column(name = "FullRecall")
    private Double  fullRecall;

    @Column(name = "MissingF1Score")
    private Double  missingF1Score;

    @Column(name = "MissingPrecision")
    private Double  missingPrecision;

    @Column(name = "MissingRecall")
    private Double  missingRecall;

    @Column(name = "ExecutionGraphF1Score")
    private Double  executionGraphF1Score;

    @Column(name = "ExecutionGraphPrecision")
    private Double  executionGraphPrecision;

    @Column(name = "ExecutionGraphRecall")
    private Double  executionGraphRecall;

    public FullMatchingResultsEntity() {
    }

    public FullMatchingResultsEntity(String project, String bug, String testCase, String algorithm, String filterPercentage, String filterHeuristic, String iteration, FullMatchingMetrics matchingMetrics) {
        this.matchingResultsKey = new MatchingResultsKey(project, bug, testCase, algorithm, filterPercentage, filterHeuristic, iteration);
        this.fullF1Score = matchingMetrics.get("F1ScoreFull");
        this.fullPrecision = matchingMetrics.get("PrecisionFull");
        this.fullRecall = matchingMetrics.get("RecallFull");
        this.missingF1Score = matchingMetrics.get("F1ScoreMissing");
        this.missingPrecision = matchingMetrics.get("PrecisionMissing");
        this.missingRecall = matchingMetrics.get("RecallMissing");
        this.executionGraphF1Score = matchingMetrics.get("F1ScoreExecutionGraph");
        this.executionGraphPrecision = matchingMetrics.get("PrecisionExecutionGraph");
        this.executionGraphRecall = matchingMetrics.get("RecallExecutionGraph");
    }

    public MatchingResultsKey getResultsKey() {
        return matchingResultsKey;
    }

    public void setResultsKey(MatchingResultsKey matchingResultsKey) {
        this.matchingResultsKey = matchingResultsKey;
    }


}
