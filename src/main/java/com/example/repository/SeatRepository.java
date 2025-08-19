package com.example.repository;

import com.example.entity.Booking;
import com.example.entity.Seat;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@ApplicationScoped
public class SeatRepository {

    @PersistenceContext
    private EntityManager em;

    public void create(Seat seat) {
        em.persist(seat);
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

    /** All seats in the event's venue that are still AVAILABLE. */
    public List<Seat> findAvailableSeatsByEvent(Integer eventId) {
        return em.createQuery("""
                SELECT s FROM Seat s
                WHERE s.venue.venueId = (
                    SELECT e.venue.venueId FROM Event e WHERE e.eventId = :eventId
                )
                AND s.seatId NOT IN (
                    SELECT t.seat.seatId
                    FROM Ticket t
                    WHERE t.event.eventId = :eventId
                    AND t.status <> 'AVAILABLE'
                )
                """, Seat.class)
                .setParameter("eventId", eventId)
                .getResultList();
    }

    /** Check if any of the seats are already SOLD or PENDING for a given event.  */
    public boolean existsLockedOrSold(Integer eventId, List<Integer> seatIds) {
        Long count = em.createQuery("""
                SELECT COUNT(t)
                FROM Ticket t
                WHERE t.event.eventId = :eventId
                AND t.seat.seatId IN :seatIds
                AND t.status <> 'AVAILABLE'
                """, Long.class)
                .setParameter("eventId", eventId)
                .setParameter("seatIds", seatIds)
                .getSingleResult();
        return count > 0;
    }

    /** Lock seats (status := PENDING) */
    public void lockSeats(Integer eventId, List<Integer> seatIds) {
        em.createQuery("""
                UPDATE Ticket t
                SET t.status = 'PENDING'
                WHERE t.event.eventId = :eventId
                AND t.seat.seatId IN :seatIds
                """)
                .setParameter("eventId", eventId)
                .setParameter("seatIds", seatIds)
                .executeUpdate();
    }

    /** Mark the seats in this booking as SOLD */
    public void markSeatsAsSold(Booking booking) {
        em.createQuery("""
                UPDATE Ticket t
                SET t.status = 'SOLD'
                WHERE t.booking.bookingId = :bookingId
                """)
                .setParameter("bookingId", booking.getBookingId())
                .executeUpdate();
    }

    /** Release seats back to AVAILABLE for a failed/cancelled booking */
    public void releaseSeats(Booking booking) {
        em.createQuery("""
                UPDATE Ticket t
                SET t.status = 'AVAILABLE',
                    t.booking = NULL
                WHERE t.booking.bookingId = :bookingId
                """)
                .setParameter("bookingId", booking.getBookingId())
                .executeUpdate();
    }
}
