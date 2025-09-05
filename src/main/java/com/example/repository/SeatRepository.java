package com.example.repository;

import com.example.common.CrudRepository;
import com.example.domain.Booking;
import com.example.domain.Seat;
import jakarta.inject.Inject;              // HK2 / JSR-330
import jakarta.persistence.EntityManager;

import java.util.List;

public class SeatRepository implements CrudRepository<Seat, Integer> {

    @Inject
    EntityManager em;

    @Override
    public Seat create(Seat seat) {
        try {
            em.getTransaction().begin();
            em.persist(seat);
            em.getTransaction().commit();
            return seat;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public Seat findById(Integer id) {
        return em.find(Seat.class, id);
    }

    @Override
    public List<Seat> findAll() {
        return em.createQuery("SELECT s FROM Seat s", Seat.class).getResultList();
    }

    @Override
    public Seat update(Seat seat) {
        try {
            em.getTransaction().begin();
            Seat merged = em.merge(seat);
            em.getTransaction().commit();
            return merged;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void delete(Seat seat) {
        try {
            em.getTransaction().begin();
            Seat managed = em.contains(seat) ? seat : em.merge(seat);
            em.remove(managed);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        }
    }

    /** All available seats for an event (same venue, not reserved/sold). */
    public List<Seat> findAvailableSeatsByEvent(Integer eventId) {
        return em.createQuery("""
                SELECT s FROM Seat s
                WHERE s.venue.id = (
                    SELECT e.venue.id FROM Event e WHERE e.id = :eventId
                )
                AND s.id NOT IN (
                    SELECT t.seat.id
                    FROM Ticket t
                    WHERE t.event.id = :eventId
                    AND t.status <> 'AVAILABLE'
                )
                """, Seat.class)
                .setParameter("eventId", eventId)
                .getResultList();
    }

    /** True if any of the given seats for the event are not AVAILABLE. */
    public boolean existsLockedOrSold(Integer eventId, List<Integer> seatIds) {
        Long count = em.createQuery("""
                SELECT COUNT(t)
                FROM Ticket t
                WHERE t.event.id = :eventId
                AND t.seat.id IN :seatIds
                AND t.status <> 'AVAILABLE'
                """, Long.class)
                .setParameter("eventId", eventId)
                .setParameter("seatIds", seatIds)
                .getSingleResult();
        return count > 0;
    }

    /** Mark seats as PENDING (lock) for an event. */
    public void lockSeats(Integer eventId, List<Integer> seatIds) {
        try {
            em.getTransaction().begin();
            em.createQuery("""
                    UPDATE Ticket t
                    SET t.status = 'PENDING'
                    WHERE t.event.id = :eventId
                    AND t.seat.id IN :seatIds
                    """)
                    .setParameter("eventId", eventId)
                    .setParameter("seatIds", seatIds)
                    .executeUpdate();
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        }
    }

    /** Mark all tickets of a booking as SOLD. */
    public void markSeatsAsSold(Booking booking) {
        try {
            em.getTransaction().begin();
            em.createQuery("""
                    UPDATE Ticket t
                    SET t.status = 'SOLD'
                    WHERE t.booking.id = :bookingId
                    """)
                    .setParameter("bookingId", booking.getId())
                    .executeUpdate();
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        }
    }

    /** Release seats back to AVAILABLE for a failed/cancelled booking. */
    public void releaseSeats(Booking booking) {
        try {
            em.getTransaction().begin();
            em.createQuery("""
                    UPDATE Ticket t
                    SET t.status = 'AVAILABLE',
                        t.booking = NULL
                    WHERE t.booking.id = :bookingId
                    """)
                    .setParameter("bookingId", booking.getId())
                    .executeUpdate();
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        }
    }
}
