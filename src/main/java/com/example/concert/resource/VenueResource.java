package com.example.concert.resource;

import com.example.common.AbstractResource;
import com.example.domain.Seat;
import com.example.domain.User;
import com.example.domain.Venue;
import com.example.service.UserService;
import com.example.service.VenueService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Path("/venues")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VenueResource extends AbstractResource<Venue, Integer> {

    @Inject private VenueService venueService;
    @Inject private UserService userService;


    @Override
    protected List<Venue> findAll() { return venueService.findAll(); }

    @Override
    protected Venue findById(Integer id) { return venueService.findById(id); }


    @Override
    protected Venue create(Venue entity) {
        throw new NotAllowedException("Use POST /venues/create with actorId to create a venue.");
    }

    @Override
    protected Venue update(Venue entity) { return venueService.update(entity); }

    @Override
    protected void delete(Venue entity) { venueService.delete(entity); }


    public static class VenueCreateRequest {
        public Integer actorId;   // user performing the action
        public String  name;
        public String  address;
        public Integer capacity;
    }

    @POST
    @Path("/create")
    public Response createWithActor(VenueCreateRequest req) {
        if (req == null || req.actorId == null || req.name == null || req.name.isBlank()) {
            throw new BadRequestException("actorId and name are required.");
        }
        User actor = userService.findById(req.actorId);
        if (actor == null) throw new NotFoundException("Actor user not found: " + req.actorId);

        Venue v = new Venue();
        v.setName(req.name.trim());
        v.setAddress(req.address);
        if (req.capacity != null) v.setCapacity(req.capacity);

        Venue created = venueService.createVenue(actor, v);

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("id", created.getId());
        payload.put("name", created.getName());
        payload.put("address", created.getAddress());
        payload.put("capacity", created.getCapacity());
        return Response.status(Response.Status.CREATED).entity(payload).build();
    }


    @PUT
    @Path("/{id}/seats")
    public Response updateSeatingLayout(@PathParam("id") Integer id, List<Seat> seats) {
        Venue updated = venueService.updateSeatingLayout(id, seats);
        return Response.ok(updated).build();
    }
}
