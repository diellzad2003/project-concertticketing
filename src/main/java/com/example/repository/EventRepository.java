package com.example.repository;

import com.example.entity.Event;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class EventRepository {

    @PersistenceContext
    private EntityManager em;

    public void create(Event event) {
        em.persist(event);
    }

    public Event findById(Integer id) {
        return em.find(Event.class, id);
    }

    public List<Event> findAll() {
        return em.createQuery("SELECT e FROM Event e", Event.class)
                .getResultList();
    }

    public Event update(Event event) {
        return em.merge(event);
    }

    public void delete(Event event) {
        if (!em.contains(event)) {
            event = em.merge(event);
        }
        em.remove(event);
    }
}
