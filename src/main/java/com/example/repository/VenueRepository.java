package com.example.repository;

import com.example.common.CrudRepository;
import com.example.domain.Venue;
import jakarta.inject.Inject;              // HK2 / JSR-330
import jakarta.persistence.EntityManager;

import java.util.List;

public class VenueRepository implements CrudRepository<Venue, Integer> {

    @Inject
    EntityManager em;

    @Override
    public Venue create(Venue venue) {
        try {
            em.getTransaction().begin();
            em.persist(venue);
            em.getTransaction().commit();
            return venue;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public Venue findById(Integer id) {
        return em.find(Venue.class, id);
    }

    @Override
    public List<Venue> findAll() {
        return em.createQuery("SELECT v FROM Venue v", Venue.class)
                .getResultList();
    }

    @Override
    public Venue update(Venue venue) {
        try {
            em.getTransaction().begin();
            Venue merged = em.merge(venue);
            em.getTransaction().commit();
            return merged;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void delete(Venue venue) {
        try {
            em.getTransaction().begin();
            Venue managed = em.contains(venue) ? venue : em.merge(venue);
            em.remove(managed);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        }
    }
}
