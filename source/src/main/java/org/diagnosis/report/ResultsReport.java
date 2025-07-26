package org.diagnosis.report;

import org.diagnosis.evaluation.Metric;
import org.diagnosis.evaluation.Metrics;
import org.diagnosis.report.entities.ReportEntity;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public abstract class ResultsReport extends DatabaseReport{

    public abstract void calculateMetrics();
    public abstract ReportEntity newEntity(Metric metric);
    public abstract Metrics getMetrics();

    SessionFactory getSessionFactory(Class annotatedClass) {
        return getSessionFactory(REMOTE_CFG_FILE, annotatedClass);
    }

    Session getRemoteSession(SessionFactory remoteSessionFactory) {
        Session remoteSession = remoteSessionFactory.openSession();
        return remoteSession;
    }

    public void report(Class annotatedClass) {
        SessionFactory remoteSessionFactory = getSessionFactory(annotatedClass);
        Session remoteSession = getRemoteSession(remoteSessionFactory);
        Transaction transaction = null;
        try {
            transaction = remoteSession.beginTransaction();
            this.calculateMetrics();
            remoteSession.beginTransaction();
            for (Metric metric : this.getMetrics()) {
                ReportEntity reportEntity = this.newEntity(metric);
                remoteSession.save(reportEntity);
            }
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
}
