package com.example.resource;

import com.example.common.AbstractResource;
import com.example.domain.Event;
import com.example.service.EventService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

import java.util.List;

@Path("/events")
public class EventResource extends AbstractResource<Event, Integer> {

    @Inject
    private EventService eventService;

    @Override
    protected List<Event> findAll() {
        return eventService.findAll();
    }

    @Override
    protected Event findById(Integer id) {
        return eventService.findById(id);
    }

    @Override
    protected Event create(Event entity) {
        eventService.create(entity);
        return entity;
    }

    @Override
    protected Event update(Event entity) {
        return eventService.update(entity);
    }

    @Override
    protected void delete(Event entity) {
        eventService.delete(entity);
    }
}
