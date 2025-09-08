package com.example.repository;

import com.example.common.CrudRepository;
import com.example.domain.Event;
import jakarta.inject.Inject;              // JSR-330 (works with HK2)
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
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
    public List<Event> search(String q,
                              LocalDateTime from,
                              LocalDateTime to,
                              Integer venueId,
                              int limit,
                              int offset) {

        StringBuilder jpql = new StringBuilder(
                "SELECT e FROM Event e WHERE 1=1 ");

        if (q != null && !q.isBlank()) {
            jpql.append("AND (LOWER(e.name) LIKE :q OR LOWER(e.details) LIKE :q) ");
        }
        if (from != null) {
            jpql.append("AND e.startDatetime >= :from ");
        }
        if (to != null) {
            jpql.append("AND e.startDatetime <= :to ");
        }
        if (venueId != null) {
            jpql.append("AND e.venue.id = :venueId ");
        }
        jpql.append("ORDER BY e.startDatetime ASC");

        var query = em.createQuery(jpql.toString(), Event.class);

        if (q != null && !q.isBlank()) query.setParameter("q", "%"+q.toLowerCase()+"%");
        if (from != null) query.setParameter("from", from);
        if (to != null)   query.setParameter("to", to);
        if (venueId != null) query.setParameter("venueId", venueId);

        return query.setFirstResult(offset).setMaxResults(limit).getResultList();
    }

    public List<Event> listUpcoming(int limit, int offset) {
        return em.createQuery("""
                SELECT e FROM Event e
                WHERE e.startDatetime >= CURRENT_TIMESTAMP
                ORDER BY e.startDatetime ASC
                """, Event.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }
}
