package com.example.resource;

import com.example.domain.Ticket;
import com.example.service.TicketService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/tickets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TicketResource {

    @Inject
    private TicketService ticketService;

    @GET
    public List<Ticket> getAllTickets() {
        return ticketService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getTicketById(@PathParam("id") Integer id) {
        Ticket ticket = ticketService.findById(id);
        if (ticket != null) {
            return Response.ok(ticket).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    public Response createTicket(Ticket ticket) {
        ticketService.create(ticket);
        return Response.status(Response.Status.CREATED).entity(ticket).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateTicket(@PathParam("id") Integer id, Ticket ticket) {
        ticket.setTicketId(id);
        Ticket updated = ticketService.update(ticket);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteTicket(@PathParam("id") Integer id) {
        Ticket ticket = ticketService.findById(id);
        if (ticket != null) {
            ticketService.delete(ticket);
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
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
