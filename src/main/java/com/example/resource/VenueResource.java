package com.example.resource;

import com.example.entity.Seat;
import com.example.entity.Venue;
import com.example.service.VenueService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/venues")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VenueResource {

    @Inject
    private VenueService venueService;

    @GET
    public List<Venue> getAllVenues() {
        return venueService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getVenueById(@PathParam("id") Integer id) {
        Venue venue = venueService.findById(id);
        if (venue != null) {
            return Response.ok(venue).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    public Response createVenue(Venue venue) {
        venueService.create(venue);
        return Response.status(Response.Status.CREATED).entity(venue).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateVenue(@PathParam("id") Integer id, Venue venue) {
        venue.setVenueId(id);
        Venue updated = venueService.update(venue);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteVenue(@PathParam("id") Integer id) {
        Venue venue = venueService.findById(id);
        if (venue != null) {
            venueService.delete(venue);
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }


    @PUT
    @Path("/{id}/seats")
    public Response updateSeatingLayout(@PathParam("id") Integer id, List<Seat> seats) {
        Venue updated = venueService.updateSeatingLayout(id, seats);
        return Response.ok(updated).build();
    }
}
