package com.project.event.services.event;

import com.project.event.models.Event;
import com.project.event.models.dto.EventDto;

import java.util.List;

public interface EventService {
    List<EventDto> getAllEvents(long after, String date, String title, Long groupId);

    Event getEventById(long id);

    void createEvent(Event event);

    void deleteEventById(long eventId);

    void updateEvent(Event event);

    List<Long> getEventCheckedStudentId(long eventId);
}
