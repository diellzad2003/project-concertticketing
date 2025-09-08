package com.example.concert.resource;

import com.example.common.AbstractResource;
import com.example.domain.Event;
import com.example.service.EventService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@Path("/events")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EventResource extends AbstractResource<Event, Integer> {

    @Inject
    private EventService eventService;


    @GET
    @Path("/browse")
    public Response browse(@QueryParam("date") String date,
                           @QueryParam("venue") Integer venueId,
                           @QueryParam("q") String q,
                           @QueryParam("page") @DefaultValue("1") int page,
                           @QueryParam("size") @DefaultValue("20") int size) {



        return Response.ok(Map.of(
                "page", page,
                "size", size,
                "items", List.of()
        )).build();
    }


    @GET
    @Path("/{id}/seats")
    public Response seats(@PathParam("id") Integer eventId) {

        return Response.ok(Map.of(
                "eventId", eventId,
                "sections", List.of()
        )).build();
    }


    @Override
    protected List<Event> findAll() {
        return eventService.findAll();
    }

    @Override
    protected Event findById(Integer id) {
        return eventService.findById(id);
    }

    @Override
    protected Event create(Event entity) {
        eventService.create(entity);
        return entity;
    }

    @Override
    protected Event update(Event entity) {
        return eventService.update(entity);
    }

    @Override
    protected void delete(Event entity) {
        eventService.delete(entity);
    }
    @GET
    @Path("/search")
    public Response search(@QueryParam("q") String q,
                           @QueryParam("from") String fromStr,
                           @QueryParam("to") String toStr,
                           @QueryParam("venueId") Integer venueId,
                           @DefaultValue("20") @QueryParam("limit") int limit,
                           @DefaultValue("0") @QueryParam("offset") int offset) {

        LocalDateTime from = parseOrNull(fromStr);
        LocalDateTime to   = parseOrNull(toStr);

        List<Event> result = eventService.search(q, from, to, venueId, limit, offset);
        return Response.ok(result).build();
    }


    @GET
    @Path("/upcoming")
    public Response upcoming(@DefaultValue("20") @QueryParam("limit") int limit,
                             @DefaultValue("0") @QueryParam("offset") int offset) {
        List<Event> result = eventService.listUpcoming(limit, offset);
        return Response.ok(result).build();
    }

    private LocalDateTime parseOrNull(String s) {
        if (s == null || s.isBlank()) return null;
        try { return LocalDateTime.parse(s); }
        catch (DateTimeParseException e) { throw new BadRequestException("Invalid datetime: " + s); }
    }
}
