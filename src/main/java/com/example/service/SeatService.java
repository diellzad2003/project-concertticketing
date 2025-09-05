package com.example.service;

import com.example.common.AbstractService;
import com.example.common.CrudRepository;
import com.example.domain.Seat;
import com.example.repository.SeatRepository;
import jakarta.inject.Inject;

import java.util.List;

public class SeatService extends AbstractService<Seat, Integer> {

    @Inject
    private SeatRepository seatRepository;

    @Override
    protected CrudRepository<Seat, Integer> getRepository() {
        return seatRepository;
    }

    public List<Seat> findAvailableSeatsByEvent(Integer eventId) {
        return seatRepository.findAvailableSeatsByEvent(eventId);
    }
}
