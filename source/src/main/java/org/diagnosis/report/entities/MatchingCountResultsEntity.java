package org.diagnosis.report.entities;

import org.diagnosis.evaluation.MatchingCountMetrics;
import org.diagnosis.evaluation.MatchingMetrics;

import javax.persistence.*;

@Entity
@Table(name="matching_count_results")
public class MatchingCountResultsEntity implements ReportEntity {

    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "project", column = @Column(name = "Project", length = 100)),
            @AttributeOverride(name = "bug", column = @Column(name = "Bug", length = 100)),
            @AttributeOverride(name = "filterPercentage", column = @Column(name = "FilterPercentage", length = 100)),
            @AttributeOverride(name = "filterHeuristic", column = @Column(name = "FilterHeuristic", length = 100)),
            @AttributeOverride(name = "iteration", column = @Column(name = "Iteration", length = 100)),
            @AttributeOverride(name = "testCase", column = @Column(name = "TestCase", length = 256)),
            @AttributeOverride(name = "componentsSet", column = @Column(name = "ComponentsSet", length = 100)),
            @AttributeOverride(name = "hitType", column = @Column(name = "HitType", length = 100))
    })
    private MatchingCountResultsKey matchingCountResultsKey;

    @Column(name = "FullTrace")
    private Long  fullTrace;

    @Column(name = "PartialTrace")
    private Long  partialTrace;

    @Column(name = "ReconstructedTrace")
    private Long  reconstructedTrace;

    @Column(name = "WeightedReconstructedTrace")
    private Long weightedReconstructedTrace;

    public MatchingCountResultsEntity() {
    }

    public MatchingCountResultsEntity(String project, String bug, String filterPercentage, String filterHeuristic, String iteration, String testCase, String componentsSet, String hitType, Long fullTrace, Long partialTrace, Long reconstructedTrace, Long weightedReconstructedTrace) {
        this.matchingCountResultsKey = new MatchingCountResultsKey(project, bug, filterPercentage, filterHeuristic, iteration, testCase, componentsSet, hitType);
        this.fullTrace = fullTrace;
        this.partialTrace = partialTrace;
        this.reconstructedTrace = reconstructedTrace;
        this.weightedReconstructedTrace = weightedReconstructedTrace;
    }

    public MatchingCountResultsKey getResultsKey() {
        return matchingCountResultsKey;
    }

    public void setResultsKey(MatchingCountResultsKey matchingCountResultsKey) {
        this.matchingCountResultsKey= matchingCountResultsKey;
    }


}
