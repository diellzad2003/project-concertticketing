package com.example.service;

import com.example.entity.Ticket;
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
    public void create(Ticket ticket) {
        ticketRepository.create(ticket);
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

    /**
     * Marks the ticket as SOLD and generates a unique e-ticket code.
     * (you could also email from here if needed)
     */
    @Transactional
    public Ticket confirmTicketPurchase(Integer ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket cannot be found");
        }

        ticket.setStatus("SOLD");

        // Generate unique e-ticket code
        String eTicketCode = "TCK-" + UUID.randomUUID();
        // In a real system this would be saved in a separate column or entity,
        // but for demonstration purposes we attach it to the 'status' field.
        ticket.setStatus("SOLD (" + eTicketCode + ")");

        return ticketRepository.update(ticket);
    }
}
