package com.project.event.services;

import com.project.event.models.Event;
import com.project.event.models.EventDto;

public interface EventDtoMapper {
    EventDto convert(Event event);
}
