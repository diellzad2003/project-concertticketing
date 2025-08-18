package com.example.repository;

import com.example.entity.Booking;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BookingRepository extends AbstractRepository<Booking, Integer> {
    public BookingRepository() { super(Booking.class); }
}
