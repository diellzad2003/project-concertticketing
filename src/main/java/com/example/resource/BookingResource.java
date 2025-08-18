package com.example.resource;

import com.example.entity.Booking;
import com.example.entity.Seat;
import com.example.service.BookingService;
import com.example.service.SeatService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/bookings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookingResource {

    @Inject
    private BookingService bookingService;

    @Inject
    private SeatService seatService;


    // ---- GET ALL / GET BY ID -----------------------

    @GET
    public List<Booking> getAllBookings() {
        return bookingService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getBookingById(@PathParam("id") Integer id) {
        Booking booking = bookingService.findById(id);
        if (booking != null) {
            return Response.ok(booking).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    // ---- BASIC CREATE (optional) -------------------

    @POST
    public Response createBooking(Booking booking) {
        bookingService.create(booking);
        return Response.status(Response.Status.CREATED).entity(booking).build();
    }

    // ---- RESERVE SEATS (locking mechanism) ---------

    @POST
    @Path("/reserve")
    public Response reserveSeats(@QueryParam("eventId") Integer eventId,
                                 @QueryParam("userId") Integer userId,
                                 List<Integer> seatIds) {

        // fetch Seat objects for the requested seats
        List<Seat> seats = seatIds.stream()
                .map(seatService::findById)
                .toList();

        try {
            Booking booking = bookingService.reserveSeats(eventId, seats, userId);
            return Response.ok(booking).build();
        } catch (IllegalStateException ex) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(ex.getMessage())
                    .build();
        }
    }

    // ---- UPDATE / DELETE ---------------------------

    @PUT
    @Path("/{id}")
    public Response updateBooking(@PathParam("id") Integer id, Booking booking) {
        booking.setBookingId(id);
        Booking updated = bookingService.update(booking);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteBooking(@PathParam("id") Integer id) {
        Booking booking = bookingService.findById(id);
        if (booking != null) {
            bookingService.delete(booking);
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
