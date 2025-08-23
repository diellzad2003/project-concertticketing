package com.example.resource;

import com.example.common.AbstractResource;
import com.example.domain.Seat;
import com.example.domain.Venue;
import com.example.service.VenueService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/venues")
@Produces("application/json")
@Consumes("application/json")
public class VenueResource extends AbstractResource<Venue, Integer> {

    @Inject
    private VenueService venueService;

    @Override
    protected List<Venue> findAll() {
        return venueService.findAll();
    }

    @Override
    protected Venue findById(Integer id) {
        return venueService.findById(id);
    }

    @Override
    protected Venue create(Venue entity) {
        venueService.create(entity);
        return entity;
    }

    @Override
    protected Venue update(Venue entity) {
        return venueService.update(entity);
    }

    @Override
    protected void delete(Venue entity) {
        venueService.delete(entity);
    }


    @PUT
    @Path("/{id}/seats")
    public Response updateSeatingLayout(@PathParam("id") Integer id, List<Seat> seats) {
        Venue updated = venueService.updateSeatingLayout(id, seats);
        return Response.ok(updated).build();
    }
}
