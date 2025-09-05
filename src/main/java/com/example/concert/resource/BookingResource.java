package com.example.concert.resource;

import com.example.common.AbstractResource;
import com.example.domain.Booking;
import com.example.service.BookingService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/bookings")
@Produces("application/json")
@Consumes("application/json")
public class BookingResource extends AbstractResource<Booking, Integer> {

    private final BookingService service = new BookingService();

    @Override
    protected Booking create(Booking entity) {
        return service.create(entity);
    }

    @Override
    protected void delete(Booking entity) {
        service.delete(entity);
    }

    @Override
    protected Booking findById(Integer id) {
        return service.findById(id);
    }

    @Override
    protected java.util.List<Booking> findAll() {
        return service.findAll();
    }

    @Override
    protected Booking update(Booking entity) {
        return service.update(entity);
    }
}
