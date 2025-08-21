package com.example.service;

import com.example.domain.Ticket;
import com.example.domain.TicketStatus;
import com.example.repository.TicketRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class TicketService {

    @Inject
    private TicketRepository ticketRepository;

    @Transactional
    public Ticket create(Ticket ticket) {
        // default status is AVAILABLE
        if (ticket.getStatus() == null) {
            ticket.setStatus(TicketStatus.AVAILABLE);
        }
        return ticketRepository.update(ticket);
    }

    public Ticket findById(Integer id) {
        return ticketRepository.findById(id);
    }

    public List<Ticket> findAll() {

        return ticketRepository.findAll();
    }

    @Transactional
    public Ticket update(Ticket ticket) {
        return ticketRepository.update(ticket);
    }

    @Transactional
    public void delete(Ticket ticket) {
        ticketRepository.delete(ticket);
    }


    @Transactional
    public Ticket confirmTicketPurchase(Integer ticketId) {
        Ticket ticket = ticketRepository.findByIdForUpdate(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket not found");
        }
        if (ticket.getStatus() != TicketStatus.AVAILABLE && ticket.getStatus() != TicketStatus.PENDING) {
            throw new IllegalStateException("Ticket is not available for sale");
        }

        ticket.setStatus(TicketStatus.SOLD);
        ticket.setEticketCode("TCK-" + UUID.randomUUID().toString().replaceAll("-", ""));
        return ticketRepository.update(ticket);
    }
}
