package org.diagnosis.report;

import org.diagnosis.report.entities.ReportEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public abstract class QueryReport extends DatabaseReport{

    public abstract ReportEntity newEntity();

    public void report(Class annotatedClass) {
        //SessionFactory localSessionFactory = getSessionFactory(LOCAL_CFG_FILE, annotatedClass);
        SessionFactory remoteSessionFactory = getSessionFactory(REMOTE_CFG_FILE, annotatedClass);

        //Session localSession = localSessionFactory.openSession();
        Session remoteSession = remoteSessionFactory.openSession();

        try {
            // Start a transaction
            //localSession.beginTransaction();
            remoteSession.beginTransaction();

            ReportEntity reportEntity = this.newEntity();

            // set properties of 'entity' as needed
            //localSession.persist(reportEntity);
            remoteSession.persist(reportEntity);

            // Commit transaction
            //localSession.getTransaction().commit();
            remoteSession.getTransaction().commit();
        } finally {
            //localSessionFactory.close();
            remoteSessionFactory.close();
        }
    }
}
