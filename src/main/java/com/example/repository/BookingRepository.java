package com.example.repository;

import com.example.common.CrudRepository;
import com.example.domain.Booking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class BookingRepository implements CrudRepository<Booking, Integer> {

    @PersistenceContext
    private EntityManager em;

    public Booking findById(Integer id) {
        return em.find(Booking.class, id);
    }

    public Booking findByIdForUpdate(Integer id) {
        return em.find(Booking.class, id, LockModeType.PESSIMISTIC_WRITE);
    }

    public Booking create(Booking booking) {
        em.persist(booking);
        return booking;
    }

    public Booking update(Booking booking) {
        return em.merge(booking);
    }

    public void delete(Booking booking) {
        Booking managed = em.contains(booking) ? booking : em.merge(booking);
        em.remove(managed);
    }

    public List<Booking> findAll() {
        return em.createQuery("SELECT b FROM Booking b", Booking.class).getResultList();
    }
}
