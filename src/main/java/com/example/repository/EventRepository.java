package com.example.repository;

import com.example.entity.Event;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EventRepository extends AbstractRepository<Event, Integer> {
    public EventRepository() { super(Event.class); }
}
