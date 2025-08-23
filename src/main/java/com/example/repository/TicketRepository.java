package com.example.repository;

import com.example.common.CrudRepository;
import com.example.domain.Ticket;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class TicketRepository implements CrudRepository<Ticket, Integer> {

    @PersistenceContext
    private EntityManager em;

    public Ticket findById(Integer id) {
        return em.find(Ticket.class, id);
    }

    public Ticket findByIdForUpdate(Integer id) {
        return em.find(Ticket.class, id, LockModeType.PESSIMISTIC_WRITE);
    }

    public List<Ticket> findByIdsForUpdate(List<Integer> ids) {
        return em.createQuery("SELECT t FROM Ticket t WHERE t.ticketId IN :ids", Ticket.class)
                .setParameter("ids", ids)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getResultList();
    }

    public List<Ticket> findAll() {
        return em.createQuery("SELECT t FROM Ticket t", Ticket.class)
                .getResultList();
    }

    public Ticket update(Ticket ticket) {
        return em.merge(ticket);
    }

    public void delete(Ticket ticket) {
        Ticket managed = em.contains(ticket) ? ticket : em.merge(ticket);
        em.remove(managed);
    }
}
