package com.example.resource;

import com.example.common.AbstractResource;
import com.example.domain.Seat;
import com.example.service.SeatService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/seats")
@Produces("application/json")
@Consumes("application/json")
public class SeatResource extends AbstractResource<Seat, Integer> {

    @Inject
    private SeatService seatService;

    @Override
    protected List<Seat> findAll() {
        return seatService.findAll();
    }

    @Override
    protected Seat findById(Integer id) {
        return seatService.findById(id);
    }

    @Override
    protected Seat create(Seat entity) {
        seatService.create(entity);
        return entity;
    }

    @Override
    protected Seat update(Seat entity) {
        return seatService.update(entity);
    }

    @Override
    protected void delete(Seat entity) {
        seatService.delete(entity);
    }


    @GET
    @Path("/available")
    public List<Seat> getAvailableSeats(@QueryParam("eventId") Integer eventId) {
        return seatService.findAvailableSeatsByEvent(eventId);
    }
}
