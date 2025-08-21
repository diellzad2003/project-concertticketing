package com.example.resource;

import com.example.domain.Event;
import com.example.service.EventService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/events")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EventResource {

    @Inject
    private EventService eventService;

    @GET
    public List<Event> getAllEvents() {
        return eventService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getEventById(@PathParam("id") Integer id) {
        Event event = eventService.findById(id);
        if (event != null) {
            return Response.ok(event).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    public Response createEvent(Event event) {
        eventService.create(event);
        return Response.status(Response.Status.CREATED).entity(event).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateEvent(@PathParam("id") Integer id, Event event) {
        event.setEventId(id);
        Event updated = eventService.update(event);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteEvent(@PathParam("id") Integer id) {
        Event event = eventService.findById(id);
        if (event != null) {
            eventService.delete(event);
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
