package org.diagnosis.report;

import org.diagnosis.evaluation.FullMatchingMetrics;
import org.diagnosis.evaluation.MatchingMetrics;
import org.diagnosis.evaluation.Metric;
import org.diagnosis.evaluation.Metrics;
import org.diagnosis.report.entities.FullMatchingResultsEntity;
import org.diagnosis.report.entities.MatchingResultsEntity;
import org.diagnosis.report.entities.ReportEntity;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;

import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.List;

public class FullMatchingReport extends ResultsReport{

    private final String project;
    private final String bug;
    private final String algorithm;
    private final String filterPercentage;
    private final String filterHeuristic;
    private final String iteration;
    private List<FullMatchingResultsEntity> matchingResultsEntities;

    private SessionFactory remoteSessionFactory;
    private Session remoteSession;

    public FullMatchingReport(String project, String bug, String algorithm, String filterPercentage, String filterHeuristic, String iteration) {
        this.project = project;
        this.bug = bug;
        this.algorithm = algorithm;
        this.filterPercentage = filterPercentage;
        this.filterHeuristic = filterHeuristic;
        this.iteration = iteration;
        this.matchingResultsEntities = new ArrayList<>();

    }

    public void addMetrics(String testCase, FullMatchingMetrics matchingMetrics) {
        matchingMetrics.calculateAll();
        FullMatchingResultsEntity matchingResultsEntity = new FullMatchingResultsEntity(
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

    public void report() throws HibernateException {
        this.report(FullMatchingResultsEntity.class);
    }

    @Override
    public void report(Class annotatedClass) throws HibernateException {
        remoteSessionFactory = getSessionFactory(FullMatchingResultsEntity.class);
        remoteSession = getRemoteSession(remoteSessionFactory);
        Transaction transaction = remoteSession.beginTransaction();
        try {
            int count = 0;
            for (FullMatchingResultsEntity matchingResultsEntity : matchingResultsEntities) {
                remoteSession.save(matchingResultsEntity);
                count++;
                if (count % 50 == 0) {
                    remoteSession.flush();
                    remoteSession.clear();
                }
            }
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null)
                transaction.rollback();
            throw new HibernateException(e);
        } catch (Exception e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                throw new HibernateException(e.getCause());
            }
            throw new HibernateException(e);
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
