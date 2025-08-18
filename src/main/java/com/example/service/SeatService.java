package com.example.service;

import com.example.entity.Seat;
import com.example.repository.SeatRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class SeatService {

    @Inject
    private SeatRepository seatRepository;

    @Transactional
    public void create(Seat seat) {
        seatRepository.create(seat);
    }

    public Seat findById(Integer id) {
        return seatRepository.findById(id);
    }

    @Transactional
    public Seat update(Seat seat) {
        return seatRepository.update(seat);
    }

    @Transactional
    public void delete(Seat seat) {
        seatRepository.delete(seat);
    }
}
