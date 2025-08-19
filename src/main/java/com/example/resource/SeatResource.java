package com.example.resource;

import com.example.entity.Seat;
import com.example.service.SeatService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/seats")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SeatResource {

    @Inject
    private SeatService seatService;

    @GET
    public List<Seat> getAllSeats() {
        return seatService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getSeatById(@PathParam("id") Integer id) {
        Seat seat = seatService.findById(id);
        return seat != null
                ? Response.ok(seat).build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }


    @GET
    @Path("/available")
    public List<Seat> getAvailableSeats(@QueryParam("eventId") Integer eventId) {
        return seatService.findAvailableSeatsByEvent(eventId);
    }

    @POST
    public Response createSeat(Seat seat) {
        seatService.create(seat);
        return Response.status(Response.Status.CREATED).entity(seat).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateSeat(@PathParam("id") Integer id, Seat seat) {
        seat.setSeatId(id);
        return Response.ok(seatService.update(seat)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteSeat(@PathParam("id") Integer id) {
        Seat seat = seatService.findById(id);
        if (seat != null) {
            seatService.delete(seat);
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
