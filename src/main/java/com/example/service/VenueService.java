package com.example.service;

import com.example.entity.Venue;
import com.example.repository.VenueRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

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

    @Transactional
    public Venue update(Venue venue) {
        return venueRepository.update(venue);
    }

    @Transactional
    public void delete(Venue venue) {
        venueRepository.delete(venue);
    }
}
