package com.example.concert.resource;

import com.example.common.AbstractResource;
import com.example.domain.Ticket;
import com.example.service.TicketService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

@Path("/tickets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
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
    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateEntity(@PathParam("id") Integer id, Ticket body) {

        Ticket t = ticketService.partialUpdate(id, body);

        return Response.ok(Map.of(
                "id", t.getId(),
                "status", String.valueOf(t.getStatus()),
                "price", t.getPrice(),
                "eTicketCode", t.geteTicketCode(),
                "eventId", t.getEvent() != null ? t.getEvent().getId() : null,
                "seatId",  t.getSeat()  != null ? t.getSeat().getId()  : null
        )).build();
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
