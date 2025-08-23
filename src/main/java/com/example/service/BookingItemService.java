package com.example.service;

import com.example.common.AbstractService;
import com.example.common.CrudRepository;
import com.example.domain.BookingItem;
import com.example.repository.BookingItemRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class BookingItemService extends AbstractService<BookingItem, Integer> {

    @Inject
    private BookingItemRepository bookingItemRepository;

    @Override
    protected CrudRepository<BookingItem, Integer> getRepository() {
        return bookingItemRepository;
    }
}
