package com.example.repository;

import com.example.common.CrudRepository;
import com.example.domain.Event;
import jakarta.inject.Inject;              // JSR-330 (works with HK2)
import jakarta.persistence.EntityManager;

import java.util.List;

public class EventRepository implements CrudRepository<Event, Integer> {

    @Inject
    EntityManager em;

    @Override
    public Event create(Event event) {
        try {
            em.getTransaction().begin();
            em.persist(event);
            em.getTransaction().commit();
            return event;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public Event findById(Integer id) {
        return em.find(Event.class, id);
    }

    @Override
    public List<Event> findAll() {
        return em.createQuery("SELECT e FROM Event e", Event.class)
                .getResultList();
    }

    @Override
    public Event update(Event event) {
        try {
            em.getTransaction().begin();
            Event merged = em.merge(event);
            em.getTransaction().commit();
            return merged;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void delete(Event event) {
        try {
            em.getTransaction().begin();
            Event managed = em.contains(event) ? event : em.merge(event);
            em.remove(managed);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        }
    }
}
