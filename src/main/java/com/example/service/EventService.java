package com.example.service;

import com.example.common.AbstractService;
import com.example.common.CrudRepository;
import com.example.domain.Event;
import com.example.repository.EventRepository;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;

public class EventService extends AbstractService<Event, Integer> {

    @Inject private EventRepository eventRepository;
    @Inject private EntityManager entityManager;

    @Override
    protected CrudRepository<Event, Integer> getRepository() {
        return eventRepository;
    }

    @Override
    public Event create(Event event) {

        List<Event> existing = entityManager
                .createQuery("SELECT e FROM Event e WHERE e.venue = :venue", Event.class)
                .setParameter("venue", event.getVenue())
                .getResultList();

        for (Event e : existing) {
            LocalDateTime start = e.getStartDatetime();
            LocalDateTime end = e.getEndDatetime();
            boolean overlap = event.getStartDatetime().isBefore(end)
                    && event.getEndDatetime().isAfter(start);
            if (overlap) {
                throw new IllegalStateException(
                        "Another event is already scheduled in this venue during the requested time.");
            }
        }

        return eventRepository.create(event);
    }
}
