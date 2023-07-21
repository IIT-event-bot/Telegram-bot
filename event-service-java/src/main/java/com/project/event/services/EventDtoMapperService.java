package com.project.event.services;

import com.project.event.models.Event;
import com.project.event.models.EventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventDtoMapperService implements EventDtoMapper {
    private final FeedbackService feedbackService;

    @Override
    public EventDto convert(Event event) {
        var grade = feedbackService.getEventGrade(event.getId());
        return new EventDto(
                event.getId(),
                event.getTitle(),
                event.isHasFeedback(),
                event.getEventTime(),
                grade.getGrade()
        );
    }
}
