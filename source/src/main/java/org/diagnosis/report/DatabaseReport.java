package org.diagnosis.report;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public abstract class DatabaseReport implements Report{

    static final String REMOTE_CFG_FILE = "hibernate.remote.cfg.xml";

    SessionFactory getSessionFactory(String cfgFile, Class annotatedClass) {
        Configuration cfg = new Configuration();
        cfg.configure(cfgFile);
        cfg.addAnnotatedClass(annotatedClass);

        StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder().applySettings(cfg.getProperties());

        return cfg.buildSessionFactory(registryBuilder.build());
    }
}
