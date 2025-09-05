package com.example.repository;

import com.example.common.CrudRepository;
import com.example.domain.Ticket;
import jakarta.inject.Inject;                 // JSR-330 (works with HK2)
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

import java.util.List;

public class TicketRepository implements CrudRepository<Ticket, Integer> {

    @Inject
    EntityManager em;

    @Override
    public Ticket create(Ticket ticket) {
        try {
            em.getTransaction().begin();
            em.persist(ticket);
            em.getTransaction().commit();
            return ticket;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public Ticket findById(Integer id) {
        return em.find(Ticket.class, id);
    }


    public Ticket findByIdForUpdate(Integer id) {
        return em.find(Ticket.class, id, LockModeType.PESSIMISTIC_WRITE);
    }


    public List<Ticket> findByIdsForUpdate(List<Integer> ids) {
        return em.createQuery("SELECT t FROM Ticket t WHERE t.id IN :ids", Ticket.class)
                .setParameter("ids", ids)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getResultList();
    }

    @Override
    public List<Ticket> findAll() {
        return em.createQuery("SELECT t FROM Ticket t", Ticket.class)
                .getResultList();
    }

    @Override
    public Ticket update(Ticket ticket) {
        try {
            em.getTransaction().begin();
            Ticket merged = em.merge(ticket);
            em.getTransaction().commit();
            return merged;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void delete(Ticket ticket) {
        try {
            em.getTransaction().begin();
            Ticket managed = em.contains(ticket) ? ticket : em.merge(ticket);
            em.remove(managed);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        }
    }
}
