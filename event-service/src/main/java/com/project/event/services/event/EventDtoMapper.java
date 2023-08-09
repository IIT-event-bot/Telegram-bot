package com.project.event.services.event;

import com.project.event.models.Event;
import com.project.event.models.dto.EventDto;

public interface EventDtoMapper {
    EventDto convert(Event event);
}
