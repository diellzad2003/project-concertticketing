package com.example.resource;

import com.example.common.AbstractResource;
import com.example.domain.BookingItem;
import com.example.service.BookingItemService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import java.util.List;

@Path("/booking-items")
public class BookingItemResource extends AbstractResource<BookingItem, Integer> {

    @Inject
    private BookingItemService bookingItemService;

    @Override
    protected List<BookingItem> findAll() {
        return bookingItemService.findAll();
    }

    @Override
    protected BookingItem findById(Integer id) {
        return bookingItemService.findById(id);
    }

    @Override
    protected BookingItem create(BookingItem entity) {
        bookingItemService.create(entity);
        return entity;
    }

    @Override
    protected BookingItem update(BookingItem entity) {
        return bookingItemService.update(entity);
    }

    @Override
    protected void delete(BookingItem entity) {
        bookingItemService.delete(entity);
    }
}
