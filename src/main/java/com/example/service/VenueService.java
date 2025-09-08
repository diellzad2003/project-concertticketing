package com.example.service;

import com.example.common.AbstractService;
import com.example.common.CrudRepository;
import com.example.domain.Seat;
import com.example.domain.User;
import com.example.domain.UserRole;
import com.example.domain.Venue;
import com.example.repository.VenueRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

public class VenueService extends AbstractService<Venue, Integer> {

    @Inject
    private VenueRepository venueRepository;

    @Override
    protected CrudRepository<Venue, Integer> getRepository() {
        return venueRepository;
    }

    private void ensureOrganizer(User actor) {
        if (actor == null || actor.getRoles() == null || !actor.getRoles().contains(UserRole.ORGANIZER)) {
            throw new SecurityException("Only organizers can perform this action.");
        }
    }


    @Transactional
    public Venue createVenue(User actor, Venue venue) {
        ensureOrganizer(actor);

        return venueRepository.create(venue);
    }


    @Override
    public Venue create(Venue venue) {
        throw new UnsupportedOperationException("Use createVenue(actor, venue) to enforce organizer checks.");
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
