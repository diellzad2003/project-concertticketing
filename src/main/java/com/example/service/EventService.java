package com.example.service;

import com.example.entity.Event;
import com.example.repository.EventRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class EventService {

    @Inject
    private EventRepository eventRepository;

    @Inject
    private EntityManager entityManager;

    @Transactional
    public void create(Event event) {

        List<Event> existingEvents = entityManager
                .createQuery("SELECT e FROM Event e WHERE e.venue = :venue", Event.class)
                .setParameter("venue", event.getVenue())
                .getResultList();

        for (Event e : existingEvents) {
            LocalDateTime start = e.getStartDatetime();
            LocalDateTime end = e.getEndDatetime();

            boolean overlap =
                    (event.getStartDatetime().isBefore(end)) &&
                            (event.getEndDatetime().isAfter(start));

            if (overlap) {
                throw new IllegalStateException(
                        "Another event is already scheduled in this venue during the requested time."
                );
            }
        }

        eventRepository.create(event);
    }

    public Event findById(Integer id) {
        return eventRepository.findById(id);
    }

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    @Transactional
    public Event update(Event event) {
        return eventRepository.update(event);
    }

    @Transactional
    public void delete(Event event) {
        eventRepository.delete(event);
    }
}
