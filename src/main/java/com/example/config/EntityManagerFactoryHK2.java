package com.example.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;
import org.glassfish.hk2.api.Factory;
import org.glassfish.jersey.process.internal.RequestScoped;

import java.net.URL;

public class EntityManagerFactoryHK2 implements Factory<EntityManager> {
    private static volatile EntityManagerFactory emf;

    private static EntityManagerFactory emf() {
        if (emf == null) {
            synchronized (EntityManagerFactoryHK2.class) {
                if (emf == null) {
                    // Debug: confirm persistence.xml is on classpath
                    URL pu = Thread.currentThread().getContextClassLoader()
                            .getResource("META-INF/persistence.xml");
                    if (pu == null) {
                        throw new IllegalStateException(
                                "META-INF/persistence.xml NOT found. " +
                                        "Place it under src/main/resources/META-INF/persistence.xml");
                    } else {
                        System.out.println("Found persistence.xml at: " + pu);
                    }
                    try {
                        emf = Persistence.createEntityManagerFactory("concert_ticketingPU");
                    } catch (PersistenceException e) {
                        throw new IllegalStateException(
                                "Failed to bootstrap JPA. Check PU name 'concert_ticketingPU', " +
                                        "Hibernate on classpath, and DB settings.", e);
                    }
                }
            }
        }
        return emf;
    }

    @Override @RequestScoped
    public EntityManager provide() { return emf().createEntityManager(); }

    @Override
    public void dispose(EntityManager em) {
        if (em != null && em.isOpen()) em.close();
    }
}
