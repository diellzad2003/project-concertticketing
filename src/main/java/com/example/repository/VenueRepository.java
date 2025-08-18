package com.example.repository;



import com.example.entity.Venue;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VenueRepository extends AbstractRepository<Venue, Integer> {
    public VenueRepository() { super(Venue.class); }
}
