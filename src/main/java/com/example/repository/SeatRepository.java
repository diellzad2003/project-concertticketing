package com.example.repository;

import com.example.entity.Seat;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SeatRepository extends AbstractRepository<Seat, Integer> {
    public SeatRepository() { super(Seat.class); }
}
