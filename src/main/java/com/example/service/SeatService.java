package com.example.service;

import com.example.domain.Seat;
import com.example.repository.SeatRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

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

    public List<Seat> findAll() {
        return seatRepository.findAll();
    }

    public List<Seat> findAvailableSeatsByEvent(Integer eventId) {
        return seatRepository.findAvailableSeatsByEvent(eventId);
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
