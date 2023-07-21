package com.project.event.services;

import com.project.event.models.Event;
import com.project.event.models.EventDto;

import java.util.List;

public interface EventService {
    List<EventDto> getAllEvents(String date, String title, Long groupId);

    Event getEventById(long id);

    void createEvent(Event event);

    void deleteEventById(long eventId);

    void updateEvent(Event event);

    List<Long> getEventCheckedStudentId(long eventId);
}
