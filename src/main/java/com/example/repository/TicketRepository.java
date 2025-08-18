package com.example.repository;

import com.example.entity.Ticket;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TicketRepository extends AbstractRepository<Ticket, Integer> {
    public TicketRepository() { super(Ticket.class); }
}
