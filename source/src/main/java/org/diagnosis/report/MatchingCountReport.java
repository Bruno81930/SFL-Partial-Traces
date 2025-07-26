package org.diagnosis.report;

import org.diagnosis.evaluation.MatchingCountMetrics;
import org.diagnosis.evaluation.MatchingMetrics;
import org.diagnosis.evaluation.Metric;
import org.diagnosis.evaluation.Metrics;
import org.diagnosis.evaluation.metrics.matching_count.types.Algorithm;
import org.diagnosis.evaluation.metrics.matching_count.types.ComponentSet;
import org.diagnosis.evaluation.metrics.matching_count.types.HitType;
import org.diagnosis.report.entities.MatchingCountResultsEntity;
import org.diagnosis.report.entities.MatchingCountResultsKey;
import org.diagnosis.report.entities.MatchingResultsEntity;
import org.diagnosis.report.entities.ReportEntity;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class MatchingCountReport extends ResultsReport{

    private final String project;
    private final String bug;
    private final String filterPercentage;
    private final String filterHeuristic;
    private final String iteration;
    private final List<MatchingCountResultsEntity> matchingCountResultsEntities;

    public MatchingCountReport(String project, String bug, String filterPercentage, String filterHeuristic, String iteration) {
        this.project = project;
        this.bug = bug;
        this.filterPercentage = filterPercentage;
        this.filterHeuristic = filterHeuristic;
        this.iteration = iteration;
        this.matchingCountResultsEntities = new ArrayList<>();

    }

    private void addMetricsByTypes(String testCase, MatchingCountMetrics matchingCountMetrics, HitType hitType, ComponentSet componentSet) {
        matchingCountMetrics.calculateAll();
        this.matchingCountResultsEntities.add(new MatchingCountResultsEntity(
                project,
                bug,
                filterPercentage,
                filterHeuristic,
                iteration,
                testCase,
                componentSet.name(),
                hitType.name(),
                (long) matchingCountMetrics.get(hitType.name() + componentSet.name() + Algorithm.FullTrace.name() + "Count"),
                (long) matchingCountMetrics.get(hitType.name() + componentSet.name() + Algorithm.PartialTrace.name() + "Count"),
                (long) matchingCountMetrics.get(hitType.name() + componentSet.name() + Algorithm.ReconstructedTrace.name() + "Count"),
                (long) matchingCountMetrics.get(hitType.name() + componentSet.name() + Algorithm.WeightedReconstructedTrace.name() + "Count")));
    }


    public void addMetrics(String testCase, MatchingCountMetrics matchingCountMetrics) {
        matchingCountMetrics.calculateAll();
        for (HitType hitType : HitType.values()) {
            for (ComponentSet componentSet : ComponentSet.values()) {
                addMetricsByTypes(testCase, matchingCountMetrics, hitType, componentSet);
            }
        }
    }

    public void report() {
        this.report(MatchingCountResultsEntity.class);
    }

    @Override
    public void report(Class annotatedClass) {
        SessionFactory remoteSessionFactory = getSessionFactory(MatchingCountResultsEntity.class);
        Session remoteSession = getRemoteSession(remoteSessionFactory);
        Transaction transaction = null;
        try {
            matchingCountResultsEntities.forEach(remoteSession::persist);
            transaction = remoteSession.beginTransaction();
            remoteSession.flush();
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null)
                transaction.rollback();
            e.printStackTrace();
        } finally {
            remoteSession.close();
            remoteSessionFactory.close();
        }
    }

    @Override
    public void calculateMetrics() {
    }

    @Override
    public ReportEntity newEntity(Metric metric) {
        return null;
    }

    @Override
    public Metrics getMetrics() {
        return null;
    }


}
