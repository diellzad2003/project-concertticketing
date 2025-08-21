package com.example.resource;

import com.example.domain.Booking;
import com.example.domain.Seat;
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

    // ---- Retrieve Bookings ----

    @GET
    public List<Booking> getAllBookings() {
        return bookingService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getBookingById(@PathParam("id") Integer id) {
        Booking booking = bookingService.findById(id);
        return (booking != null)
                ? Response.ok(booking).build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }

    // ---- Simple Create ----

    @POST
    public Response createBooking(Booking booking) {
        bookingService.create(booking);
        return Response.status(Response.Status.CREATED).entity(booking).build();
    }

    // ---- Reserve / Lock Seats ----
    @POST
    @Path("/reserve")
    public Response reserveSeats(@QueryParam("eventId") Integer eventId,
                                 @QueryParam("userId") Integer userId,
                                 List<Integer> seatIds) {

        // Convert seatIds -> Seat objects
        List<Seat> seats = seatIds.stream()
                .map(seatService::findById)
                .toList();
        try {
            Booking booking = bookingService.reserveSeats(eventId, seats, userId);
            return Response.ok(booking).build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(e.getMessage())
                    .build();
        }
    }

    // ---- Update / Delete ----

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
