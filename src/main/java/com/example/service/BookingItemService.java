package com.example.service;

import com.example.entity.BookingItem;
import com.example.repository.BookingItemRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class BookingItemService {

    @Inject
    private BookingItemRepository bookingItemRepository;

    @Transactional
    public void create(BookingItem item) {
        bookingItemRepository.create(item);
    }

    public BookingItem findById(Integer id) {
        return bookingItemRepository.findById(id);
    }

    public List<BookingItem> findAll() {
        return bookingItemRepository.findAll();
    }

    @Transactional
    public BookingItem update(BookingItem item) {
        return bookingItemRepository.update(item);
    }

    @Transactional
    public void delete(BookingItem item) {
        bookingItemRepository.delete(item);
    }
}
