package com.example.service;

import com.example.common.AbstractService;
import com.example.common.CrudRepository;
import com.example.domain.Seat;
import com.example.domain.Venue;
import com.example.repository.VenueRepository;
import jakarta.inject.Inject;

import java.util.List;

public class VenueService extends AbstractService<Venue, Integer> {

    @Inject
    private VenueRepository venueRepository;

    @Override
    protected CrudRepository<Venue, Integer> getRepository() {
        return venueRepository;
    }

    @Override
    public Venue create(Venue venue) {
        return venueRepository.create(venue);
    }

    @Override
    public Venue findById(Integer id) {
        return venueRepository.findById(id);
    }

    @Override
    public List<Venue> findAll() {
        return venueRepository.findAll();
    }

    @Override
    public Venue update(Venue venue) {
        return venueRepository.update(venue);
    }

    @Override
    public void delete(Venue venue) {
        venueRepository.delete(venue);
    }

    public Venue updateSeatingLayout(Integer venueId, List<Seat> seats) {
        Venue venue = venueRepository.findById(venueId);
        venue.setSeats(seats);
        return venueRepository.update(venue);
    }
}
