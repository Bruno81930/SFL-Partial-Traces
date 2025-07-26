package org.diagnosis.report;

import org.diagnosis.algorithms.entities.TestCase;
import org.diagnosis.evaluation.MatchingMetrics;
import org.diagnosis.evaluation.Metric;
import org.diagnosis.evaluation.Metrics;
import org.diagnosis.report.entities.DiagnosisResultsEntity;
import org.diagnosis.report.entities.MatchingResultsEntity;
import org.diagnosis.report.entities.ReportEntity;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class MatchingReport extends ResultsReport{

    private final String project;
    private final String bug;
    private final String algorithm;
    private final String filterPercentage;
    private final String filterHeuristic;
    private final String iteration;
    private List<MatchingResultsEntity> matchingResultsEntities;

    private SessionFactory remoteSessionFactory;
    private Session remoteSession;

    public MatchingReport(String project, String bug, String algorithm, String filterPercentage, String filterHeuristic, String iteration) {
        this.project = project;
        this.bug = bug;
        this.algorithm = algorithm;
        this.filterPercentage = filterPercentage;
        this.filterHeuristic = filterHeuristic;
        this.iteration = iteration;
        this.matchingResultsEntities = new ArrayList<>();

    }

    public void addMetrics(String testCase, MatchingMetrics matchingMetrics) {
        matchingMetrics.calculateAll();
        MatchingResultsEntity matchingResultsEntity = new MatchingResultsEntity(
                project,
                bug,
                testCase,
                algorithm,
                filterPercentage,
                filterHeuristic,
                iteration,
                matchingMetrics);
        matchingResultsEntities.add(matchingResultsEntity);
    }

    public void report() {
        this.report(MatchingResultsEntity.class);
    }

    @Override
    public void report(Class annotatedClass) {
        remoteSessionFactory = getSessionFactory(MatchingResultsEntity.class);
        remoteSession = getRemoteSession(remoteSessionFactory);
        Transaction transaction = null;
        try {
            matchingResultsEntities.forEach(remoteSession::persist);
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
