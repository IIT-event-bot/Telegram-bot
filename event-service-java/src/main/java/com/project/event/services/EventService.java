package com.project.event.services;

import com.project.event.models.Event;
import com.project.event.models.EventDto;

import java.util.List;

public interface EventService {
    List<Event> getAllEvents();

    EventDto getEventById(long id);

    void createEvent(EventDto event);

    void deleteEventById(long eventId);

    void updateEvent(EventDto event);
}
