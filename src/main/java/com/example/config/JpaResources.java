package com.example.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.glassfish.hk2.api.Factory;
import org.glassfish.jersey.process.internal.RequestScoped;


public class JpaResources implements Factory<EntityManager> {
    private static final EntityManagerFactory EMF =
            Persistence.createEntityManagerFactory("concert_ticketingPU");

    @Override
    @RequestScoped
    public EntityManager provide() {
        return EMF.createEntityManager();
    }

    @Override
    public void dispose(EntityManager em) {
        if (em != null && em.isOpen()) em.close();
    }
}
