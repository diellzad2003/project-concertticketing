package com.example.repository;

import com.example.common.CrudRepository;
import com.example.domain.Booking;
import jakarta.inject.Inject;                 // JSR-330 (works with HK2)
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

import java.util.List;

public class BookingRepository implements CrudRepository<Booking, Integer> {

    @Inject
    EntityManager em;

    @Override
    public Booking findById(Integer id) {
        return em.find(Booking.class, id);
    }


    public Booking findByIdForUpdate(Integer id) {
        return em.find(Booking.class, id, LockModeType.PESSIMISTIC_WRITE);
    }

    @Override
    public Booking create(Booking booking) {
        try {
            em.getTransaction().begin();
            em.persist(booking);
            em.getTransaction().commit();
            return booking;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public Booking update(Booking booking) {
        try {
            em.getTransaction().begin();
            Booking merged = em.merge(booking);
            em.getTransaction().commit();
            return merged;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void delete(Booking booking) {
        try {
            em.getTransaction().begin();
            Booking managed = em.contains(booking) ? booking : em.merge(booking);
            em.remove(managed);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public List<Booking> findAll() {
        return em.createQuery("SELECT b FROM Booking b", Booking.class).getResultList();
    }
}
