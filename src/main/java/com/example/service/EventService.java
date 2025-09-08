package com.example.service;

import com.example.common.AbstractService;
import com.example.common.CrudRepository;
import com.example.domain.Event;
import com.example.domain.User;
import com.example.domain.UserRole;
import com.example.repository.EventRepository;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public class EventService extends AbstractService<Event, Integer> {

    @Inject private EventRepository eventRepository;
    @Inject private EntityManager entityManager;

    @Override
    protected CrudRepository<Event, Integer> getRepository() {
        return eventRepository;
    }


    private void ensureOrganizer(User actor) {
        if (actor == null || actor.getRoles() == null || !actor.getRoles().contains(UserRole.ORGANIZER)) {
            throw new SecurityException("Only organizers can perform this action.");
        }
    }


    @Transactional
    public Event createEvent(User actor, Event event) {
        ensureOrganizer(actor);


        event.setOrganizer(actor);


        List<Event> clashes = entityManager
                .createQuery("""
                    SELECT e FROM Event e
                    WHERE e.venue = :venue
                      AND e.endDatetime   > :start
                      AND e.startDatetime < :end
                """, Event.class)
                .setParameter("venue", event.getVenue())
                .setParameter("start", event.getStartDatetime())
                .setParameter("end",   event.getEndDatetime())
                .getResultList();

        if (!clashes.isEmpty()) {
            throw new IllegalStateException(
                    "Another event is already scheduled in this venue during the requested time window.");
        }

        return eventRepository.create(event);
    }


    @Override
    public Event create(Event event) {
        throw new UnsupportedOperationException("Use createEvent(actor, event) to enforce organizer checks.");
    }
    public List<Event> search(String q,
                              LocalDateTime from,
                              LocalDateTime to,
                              Integer venueId,
                              int limit,
                              int offset) {


        int safeLimit = Math.min(Math.max(limit, 1), 100);
        int safeOffset = Math.max(offset, 0);

        return eventRepository.search(q, from, to, venueId, safeLimit, safeOffset);
    }


    public List<Event> listUpcoming(int limit, int offset) {
        int safeLimit = Math.min(Math.max(limit, 1), 100);
        int safeOffset = Math.max(offset, 0);
        return eventRepository.listUpcoming(safeLimit, safeOffset);
    }
}

