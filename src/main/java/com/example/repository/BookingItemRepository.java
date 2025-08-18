package com.example.repository;

import com.example.entity.BookingItem;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BookingItemRepository extends AbstractRepository<BookingItem, Integer> {
    public BookingItemRepository() { super(BookingItem.class); }
}
