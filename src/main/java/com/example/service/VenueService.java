package com.example.service;

import com.example.entity.Seat;
import com.example.entity.Venue;
import com.example.repository.VenueRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class VenueService {

    @Inject
    private VenueRepository venueRepository;

    @Transactional
    public void create(Venue venue) {
        venueRepository.create(venue);
    }

    public Venue findById(Integer id) {
        return venueRepository.findById(id);
    }

    public List<Venue> findAll() {
        return venueRepository.findAll();
    }

    @Transactional
    public Venue update(Venue venue) {
        return venueRepository.update(venue);
    }

    @Transactional
    public void delete(Venue venue) {
        venueRepository.delete(venue);
    }


    @Transactional
    public Venue updateSeatingLayout(Integer venueId, List<Seat> seats) {
        Venue venue = venueRepository.findById(venueId);
        venue.setSeats(seats);
        return venueRepository.update(venue);
    }
}
