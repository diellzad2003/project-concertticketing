package com.example.service;

import com.example.common.AbstractService;
import com.example.common.CrudRepository;
import com.example.domain.Seat;
import com.example.domain.Venue;
import com.example.repository.VenueRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class VenueService extends AbstractService<Venue, Integer> {

    @Inject
    private VenueRepository venueRepository;
    @Override
    protected CrudRepository<Venue, Integer> getRepository() {
        return venueRepository;
    }

    @Override
    @Transactional
    public Venue create(Venue venue) {
        venueRepository.create(venue);
        return venue;
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
    @Transactional
    public Venue update(Venue venue) {
        return venueRepository.update(venue);
    }

    @Override
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
