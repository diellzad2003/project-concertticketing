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

    // returns true if any of the given seats are already locked or sold for the event
    public boolean existsLockedOrSold(Integer eventId, List<Integer> seatIds) {
        Long count = em.createQuery("""
                SELECT COUNT(s)
                FROM Seat s
                JOIN Ticket t ON t.seat.seatId = s.seatId
                WHERE t.event.eventId = :eventId
                AND s.seatId IN :seatIds
                """, Long.class)
                .setParameter("eventId", eventId)
                .setParameter("seatIds", seatIds)
                .getSingleResult();
        return count > 0;
    }

    // mark seats as PENDING
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

    // mark seats as SOLD
    public void markSeatsAsSold(Booking booking) {
        em.createQuery("""
                UPDATE Ticket t
                SET t.status = 'SOLD'
                WHERE t.booking.bookingId = :bookingId
                """)
                .setParameter("bookingId", booking.getBookingId())
                .executeUpdate();
    }

    // release seats (back to AVAILABLE)
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
