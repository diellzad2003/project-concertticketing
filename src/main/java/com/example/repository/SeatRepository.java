package com.example.repository;

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
    public List<Seat> findAvailableSeatsByEvent(Integer eventId) {
        return em.createQuery("""
                SELECT s FROM Seat s
                WHERE s.venue.venueId = (
                    SELECT e.venue.venueId FROM Event e WHERE e.eventId = :eventId
                )
                AND s.seatId NOT IN (
                    SELECT t.seat.seatId FROM Ticket t\s
                    WHERE t.event.eventId = :eventId AND t.status <> 'AVAILABLE'
                )
               \s""", Seat.class)
                .setParameter("eventId", eventId)
                .getResultList();
    }
}
