package com.example.concert.resource;

import com.example.common.AbstractResource;
import com.example.domain.Booking;
import com.example.domain.Payment;
import com.example.domain.Seat;
import com.example.service.BookingService;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.process.internal.RequestScoped;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequestScoped
@Path("/bookings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookingResource extends AbstractResource<Booking, Integer> {

    @Inject
    private BookingService service;




    public static class ReserveRequest {
        public Integer userId;
        public Integer eventId;
        public List<Integer> ticketIds;
    }


    public static class ReserveSeatsRequest {
        public Integer userId;
        public Integer eventId;
        public List<Integer> seatIds;
    }


    public static class PurchaseRequest {
        public Integer userId;
        public Integer eventId;
        public List<Integer> ticketIds;

        public String method;
    }


    public static class ConfirmRequest {

        public String method;
    }




    @POST
    @Path("/reserve")
    @Transactional
    public Response reserve(ReserveRequest req) {
        if (req == null || req.userId == null || req.eventId == null || req.ticketIds == null || req.ticketIds.isEmpty()) {
            throw new BadRequestException("userId, eventId and ticketIds are required.");
        }
        Booking booking = service.reserveTickets(req.userId, req.eventId, req.ticketIds);
        return Response.status(Response.Status.CREATED)
                .entity(Map.of(
                        "bookingId", booking.getId(),
                        "status", String.valueOf(booking.getStatus()),
                        "total", booking.getTotalAmount(),
                        "expiresAt", booking.getReservationExpiresAt()
                ))
                .build();
    }


    @POST
    @Path("/reserve-seats")
    @Transactional
    public Response reserveBySeats(ReserveSeatsRequest req) {
        if (req == null || req.userId == null || req.eventId == null || req.seatIds == null || req.seatIds.isEmpty()) {
            throw new BadRequestException("userId, eventId and seatIds are required.");
        }

        List<Seat> seats = req.seatIds.stream().map(id -> {
            Seat s = new Seat();
            s.setId(id);
            return s;
        }).collect(Collectors.toList());

        Booking booking = service.reserveSeats(req.eventId, seats, req.userId);
        return Response.status(Response.Status.CREATED)
                .entity(Map.of(
                        "bookingId", booking.getId(),
                        "status", String.valueOf(booking.getStatus()),
                        "total", booking.getTotalAmount(),
                        "expiresAt", booking.getReservationExpiresAt()
                ))
                .build();
    }


    @POST
    @Path("/{id}/confirm")
    @Transactional
    public Response confirm(@PathParam("id") Integer bookingId, ConfirmRequest req) {
        if (bookingId == null) throw new BadRequestException("bookingId required.");
        if (req == null || req.method == null || req.method.isBlank()) {
            throw new BadRequestException("Payment method is required.");
        }

        Payment payment = new Payment();
        payment.setMethod(req.method.trim());

        Booking booking = service.confirmPayment(bookingId, payment);

        return Response.ok(Map.of(
                "bookingId", booking.getId(),
                "status", String.valueOf(booking.getStatus()),
                "total", booking.getTotalAmount(),
                "transactionStatus", booking.getPayment() != null ? booking.getPayment().getStatus() : null,
                "transactionDate", booking.getPayment() != null ? booking.getPayment().getTransactionDate() : null,
                "tickets", booking.getTickets()   // caution: if you expose entities directly, ensure JSON serialization is fine
        )).build();
    }


    @POST
    @Path("/purchase")
    @Transactional
    public Response purchase(PurchaseRequest req) {
        if (req == null || req.userId == null || req.eventId == null || req.ticketIds == null || req.ticketIds.isEmpty()) {
            throw new BadRequestException("userId, eventId and ticketIds are required.");
        }
        if (req.method == null || req.method.isBlank()) {
            throw new BadRequestException("Payment method is required.");
        }

        Booking booking = service.purchase(req.userId, req.eventId, req.ticketIds, req.method.trim());

        return Response.status(Response.Status.CREATED)
                .entity(Map.of(
                        "bookingId", booking.getId(),
                        "status", String.valueOf(booking.getStatus()),
                        "total", booking.getTotalAmount(),
                        "transactionStatus", booking.getPayment() != null ? booking.getPayment().getStatus() : null,
                        "transactionDate", booking.getPayment() != null ? booking.getPayment().getTransactionDate() : null,
                        "tickets", booking.getTickets()
                ))
                .build();
    }


    @POST
    @Path("/{id}/cancel")
    @Transactional
    public Response cancel(@PathParam("id") Integer bookingId) {
        if (bookingId == null) throw new BadRequestException("bookingId required.");
        service.cancelBooking(bookingId);
        return Response.ok(Map.of("ok", true)).build();
    }

    @POST
    @Path("/expire")
    @Transactional
    public Response expire() {
        service.expireOldReservations();
        return Response.ok(Map.of("ok", true, "ranAt", LocalDateTime.now())).build();
    }



    @Override
    protected Booking create(Booking entity) { return service.create(entity); }

    @Override
    protected void delete(Booking entity) { service.delete(entity); }

    @Override
    protected Booking findById(Integer id) { return service.findById(id); }

    @Override
    protected List<Booking> findAll() { return service.findAll(); }

    @Override
    protected Booking update(Booking entity) { return service.update(entity); }
}
