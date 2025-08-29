package com.example.resource;

import com.example.common.AbstractResource;
import com.example.domain.Booking;
import com.example.domain.Payment;
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
public class BookingResource extends AbstractResource<Booking, Integer> {

    @Inject
    private BookingService bookingService;

    @Inject
    private SeatService seatService;

    @Override
    protected List<Booking> findAll() {
        return bookingService.findAll();
    }

    @Override
    protected Booking findById(Integer id) {
        return bookingService.findById(id);
    }

    @Override
    protected Booking create(Booking entity) {
        bookingService.create(entity);
        return entity;
    }

    @Override
    protected Booking update(Booking entity) {
        return bookingService.update(entity);
    }

    @Override
    protected void delete(Booking entity) {
        bookingService.delete(entity);
    }


    @POST
    @Path("/reserve")
    public Response reserveSeats(@QueryParam("eventId") Integer eventId,
                                 @QueryParam("userId") Integer userId,
                                 List<Integer> seatIds) {
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


    @POST
    @Path("/{id}/confirm")
    public Response confirmPayment(@PathParam("id") Integer bookingId, Payment payment) {
        try {
            Booking confirmed = bookingService.confirmPayment(bookingId, payment);
            return Response.ok(confirmed).build();
        } catch (IllegalStateException | IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }


    @POST
    @Path("/{id}/cancel")
    public Response cancelBooking(@PathParam("id") Integer bookingId) {
        try {
            bookingService.cancelBooking(bookingId);
            return Response.noContent().build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(e.getMessage())
                    .build();
        }
    }


    @POST
    @Path("/expire")
    public Response expireReservations() {
        bookingService.expireOldReservations();
        return Response.noContent().build();
    }
}
