package com.example.service;

import com.example.common.AbstractService;
import com.example.common.CrudRepository;
import com.example.domain.*;
import com.example.repository.TicketRepository;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.UUID;

public class TicketService extends AbstractService<Ticket, Integer> {

    @Inject private TicketRepository ticketRepository;
    @Inject private EntityManager em;

    @Override
    protected CrudRepository<Ticket, Integer> getRepository() {
        return ticketRepository;
    }

    @Override
    public Ticket create(Ticket ticket) {
        if (ticket.getStatus() == null) {
            ticket.setStatus(TicketStatus.AVAILABLE);
        }
        return ticketRepository.create(ticket);
    }
    @Transactional
    public Ticket partialUpdate(Integer id, Ticket patch) {
        var t = em.find(Ticket.class, id, LockModeType.PESSIMISTIC_WRITE);
        if (t == null) throw new NotFoundException("Ticket " + id + " not found");

        if (patch.geteTicketCode() != null) t.seteTicketCode(patch.geteTicketCode());
        if (patch.getStatus() != null)      t.setStatus(patch.getStatus());
        if (patch.getPrice() != null)       t.setPrice(patch.getPrice());

        if (patch.getEvent() != null && patch.getEvent().getId() != null)
            t.setEvent(em.getReference(Event.class, patch.getEvent().getId()));
        if (patch.getSeat() != null && patch.getSeat().getId() != null)
            t.setSeat(em.getReference(Seat.class, patch.getSeat().getId()));
        if (patch.getBooking() != null && patch.getBooking().getId() != null)
            t.setBooking(em.getReference(Booking.class, patch.getBooking().getId()));

        return t;
    }


    public Ticket confirmTicketPurchase(Integer ticketId) {
        try {
            em.getTransaction().begin();
            Ticket ticket = em.find(Ticket.class, ticketId, LockModeType.PESSIMISTIC_WRITE);
            if (ticket == null) throw new IllegalArgumentException("Ticket not found");

            if (ticket.getStatus() != TicketStatus.AVAILABLE &&
                    ticket.getStatus() != TicketStatus.PENDING) {
                throw new IllegalStateException("Ticket is not available for sale");
            }

            ticket.setStatus(TicketStatus.SOLD);
            ticket.seteTicketCode("TCK-" + UUID.randomUUID().toString().replace("-", ""));
            em.merge(ticket);

            em.getTransaction().commit();
            return ticket;
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        }
    }
}
