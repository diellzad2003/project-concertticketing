package com.example.service;

import com.example.common.AbstractService;
import com.example.common.CrudRepository;
import com.example.domain.Ticket;
import com.example.domain.TicketStatus;
import com.example.repository.TicketRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class TicketService extends AbstractService<Ticket, Integer> {

    @Inject
    private TicketRepository ticketRepository;

    @Override
    protected CrudRepository<Ticket, Integer> getRepository() {
        return ticketRepository;
    }


    @Override
    @Transactional
    public Ticket create(Ticket ticket) {
        if (ticket.getStatus() == null) {
            ticket.setStatus(TicketStatus.AVAILABLE);
        }
        return ticketRepository.update(ticket);
    }


    @Transactional
    public Ticket confirmTicketPurchase(Integer ticketId) {
        Ticket ticket = ticketRepository.findByIdForUpdate(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket not found");
        }
        if (ticket.getStatus() != TicketStatus.AVAILABLE &&
                ticket.getStatus() != TicketStatus.PENDING) {
            throw new IllegalStateException("Ticket is not available for sale");
        }

        ticket.setStatus(TicketStatus.SOLD);
        ticket.setEticketCode("TCK-" + UUID.randomUUID().toString().replaceAll("-", ""));
        return ticketRepository.update(ticket);
    }
}
