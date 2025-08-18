package com.example.repository;

import com.example.entity.Ticket;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class TicketRepository {

    @PersistenceContext
    private EntityManager em;

    public void create(Ticket ticket) {
        em.persist(ticket);
    }

    public Ticket findById(Integer id) {
        return em.find(Ticket.class, id);
    }

    public List<Ticket> findAll() {
        return em.createQuery("SELECT t FROM Ticket t", Ticket.class).getResultList();
    }

    public Ticket update(Ticket ticket) {
        return em.merge(ticket);
    }

    public void delete(Ticket ticket) {
        if (!em.contains(ticket)) {
            ticket = em.merge(ticket);
        }
        em.remove(ticket);
    }
}
