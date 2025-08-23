package com.example.repository;

import com.example.common.CrudRepository;
import com.example.domain.User;
import com.example.domain.Venue;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class VenueRepository implements CrudRepository<Venue, Integer> {

    @PersistenceContext
    private EntityManager em;

    public Venue create(Venue venue) {
        em.persist(venue);
        return venue;
    }

    public Venue findById(Integer id) {
        return em.find(Venue.class, id);
    }

    public List<Venue> findAll() {
        return em.createQuery("SELECT v FROM Venue v", Venue.class)
                .getResultList();
    }

    public Venue update(Venue venue) {
        return em.merge(venue);
    }

    public void delete(Venue venue) {
        if (!em.contains(venue)) {
            venue = em.merge(venue);
        }
        em.remove(venue);
    }
}
