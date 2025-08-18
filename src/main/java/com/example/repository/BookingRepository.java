package com.example.repository;

import com.example.entity.Booking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class BookingRepository {

    @PersistenceContext
    private EntityManager em;

    public void create(Booking booking) {
        em.persist(booking);
    }

    public Booking findById(Integer id) {
        return em.find(Booking.class, id);
    }

    public List<Booking> findAll() {
        return em.createQuery("SELECT b FROM Booking b", Booking.class)
                .getResultList();
    }

    public Booking update(Booking booking) {
        return em.merge(booking);
    }

    public void delete(Booking booking) {
        if (!em.contains(booking)) {
            booking = em.merge(booking);
        }
        em.remove(booking);
    }
}
