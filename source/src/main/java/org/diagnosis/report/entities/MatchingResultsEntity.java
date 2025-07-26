package org.diagnosis.report.entities;

import org.diagnosis.evaluation.MatchingMetrics;

import javax.persistence.*;

@Entity
@Table(name="matching_execution_graph_results")
public class MatchingResultsEntity implements ReportEntity {

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

    @Column(name = "TruePositives")
    private Double  truePositives;

    @Column(name = "TrueNegatives")
    private Double  trueNegatives;

    @Column(name = "FalsePositives")
    private Double  falsePositives;

    @Column(name = "FalseNegatives")
    private Double  falseNegatives;

    @Column(name = "PrecisionMatching")
    private Double  precision;

    @Column(name = "RecallMatching")
    private Double  recall;

    @Column(name = "F1Score")
    private Double  f1score;

    @Column(name = "Specificity")
    private Double  specificity;

    @Column(name = "Accuracy")
    private Double  accuracy;

    @Column(name = "FalseDiscoveryRate")
    private Double  falseDiscoveryRate;

    @Column(name = "FalsePositiveRate")
    private Double  falsePositiveRate;

    @Column(name = "MissRate")
    private Double  missRate;

    @Column(name = "FalseNegativeRate")
    private Double  negativePredictiveValue;

    @Column(name = "GroundTruthHitsActual")
    private Double  groundTruthHitsActual;

    @Column(name = "GroundTruthHitsPredicted")
    private Double  groundTruthHitsPredicted;

    public MatchingResultsEntity() {
    }

    public MatchingResultsEntity(String project, String bug, String testCase, String algorithm, String filterPercentage, String filterHeuristic, String iteration, MatchingMetrics matchingMetrics) {
        this.matchingResultsKey = new MatchingResultsKey(project, bug, testCase, algorithm, filterPercentage, filterHeuristic, iteration);
        this.truePositives = matchingMetrics.get("TruePositives");
        this.trueNegatives = matchingMetrics.get("TrueNegatives");
        this.falsePositives = matchingMetrics.get("FalsePositives");
        this.falseNegatives = matchingMetrics.get("FalseNegatives");
        this.precision = matchingMetrics.get("Precision");
        this.recall = matchingMetrics.get("Recall");
        this.f1score = matchingMetrics.get("F1Score");
        this.specificity = matchingMetrics.get("Specificity");
        this.accuracy = matchingMetrics.get("Accuracy");
        this.falseDiscoveryRate = matchingMetrics.get("FalseDiscoveryRate");
        this.falsePositiveRate = matchingMetrics.get("FalsePositiveRate");
        this.missRate = matchingMetrics.get("MissRate");
        this.negativePredictiveValue = matchingMetrics.get("NegativePredictiveValue");
        this.groundTruthHitsActual = matchingMetrics.get("GroundTruthHitsActual");
        this.groundTruthHitsPredicted = matchingMetrics.get("GroundTruthHitsPredicted");
    }

    public MatchingResultsKey getResultsKey() {
        return matchingResultsKey;
    }

    public void setResultsKey(MatchingResultsKey matchingResultsKey) {
        this.matchingResultsKey = matchingResultsKey;
    }


}
