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
import jakarta.ws.rs.NotFoundException;

import java.util.ArrayList;
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
    public Venue updateSeatingLayout(User actor, Integer venueId, List<Seat> seats) {
        ensureOrganizer(actor);
        return doUpdateSeatingLayout(venueId, seats);
    }


    @Transactional
    public Venue updateSeatingLayout(Integer venueId, List<Seat> seats) {
        return doUpdateSeatingLayout(venueId, seats);
    }

    private Venue doUpdateSeatingLayout(Integer venueId, List<Seat> seats) {
        Venue venue = venueRepository.findById(venueId);
        if (venue == null) {
            throw new NotFoundException("Venue " + venueId + " not found");
        }
        if (seats == null) seats = List.of();


        List<Seat> newSeats = new ArrayList<>(seats.size());
        for (Seat s : seats) {
            s.setVenue(venue);

            if (s.getId() != null && s.getId() <= 0) s.setId(null);
            newSeats.add(s);
        }

        venue.getSeats().clear();
        venue.getSeats().addAll(newSeats);

        return venueRepository.update(venue);
    }
}
