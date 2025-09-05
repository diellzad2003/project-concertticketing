package com.example.concert.resource;

import com.example.common.AbstractResource;
import com.example.domain.Ticket;
import com.example.service.TicketService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/tickets")
@Produces("application/json")
@Consumes("application/json")
public class TicketResource extends AbstractResource<Ticket, Integer> {

    @Inject
    private TicketService ticketService;

    @Override
    protected List<Ticket> findAll() {
        return ticketService.findAll();
    }

    @Override
    protected Ticket findById(Integer id) {
        return ticketService.findById(id);
    }

    @Override
    protected Ticket create(Ticket entity) {
        ticketService.create(entity);
        return entity;
    }

    @Override
    protected Ticket update(Ticket entity) {
        return ticketService.update(entity);
    }

    @Override
    protected void delete(Ticket entity) {
        ticketService.delete(entity);
    }


    @POST
    @Path("/{id}/confirm")
    public Response confirmPurchase(@PathParam("id") Integer id) {
        try {
            Ticket updated = ticketService.confirmTicketPurchase(id);
            return Response.ok(updated).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }
}
