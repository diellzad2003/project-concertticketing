package com.example.repository;

import com.example.common.CrudRepository;
import com.example.domain.Booking;
import com.example.domain.Seat;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@ApplicationScoped
public class SeatRepository implements CrudRepository<Seat, Integer> {

    @PersistenceContext
    private EntityManager em;

    public Seat create(Seat seat) {
        em.persist(seat);
        return seat;
    }

    public Seat findById(Integer id) {
        return em.find(Seat.class, id);
    }

    public List<Seat> findAll() {
        return em.createQuery("SELECT s FROM Seat s", Seat.class).getResultList();
    }

    public Seat update(Seat seat) {
        return em.merge(seat);
    }

    public void delete(Seat seat) {
        if (!em.contains(seat)) {
            seat = em.merge(seat);
        }
        em.remove(seat);
    }


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


    public void lockSeats(Integer eventId, List<Integer> seatIds) {
        em.createQuery("""
                UPDATE Ticket t
                SET t.status = 'PENDING'
                WHERE t.event.id = :eventId
                AND t.seat.id IN :seatIds
                """)
                .setParameter("eventId", eventId)
                .setParameter("seatIds", seatIds)
                .executeUpdate();
    }
    public void markSeatsAsSold(Booking booking) {
        em.createQuery("""
                UPDATE Ticket t
                SET t.status = 'SOLD'
                WHERE t.booking.id = :bookingId
                """)
                .setParameter("bookingId", booking.getId())
                .executeUpdate();
    }

    /** Release seats back to AVAILABLE for a failed/cancelled booking */
    public void releaseSeats(Booking booking) {
        em.createQuery("""
                UPDATE Ticket t
                SET t.status = 'AVAILABLE',
                    t.booking = NULL
                WHERE t.booking.id = :bookingId
                """)
                .setParameter("bookingId", booking.getId())
                .executeUpdate();
    }
}
