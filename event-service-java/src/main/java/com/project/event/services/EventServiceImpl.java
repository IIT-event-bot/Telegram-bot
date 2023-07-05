package com.project.event.services;

import com.project.event.models.Event;
import com.project.event.models.EventDto;
import com.project.event.models.EventType;
import com.project.event.repositories.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository repository;

    @Override
    public List<Event> getAllEvents() {//TODO добавить фильтры
        return repository.findAll();
    }

    @Override
    public EventDto getEventById(long id) {
        var event = repository.getEventById(id);
        return convertEventToEventDto(event);
    }

    @Override
    @Transactional
    public void createEvent(EventDto dto) {
        validateEvent(dto);
        var event = convertDtoToEvent(dto);
        repository.save(event);//TODO
    }

    @Override
    public void deleteEventById(long eventId) {
//TODO
    }

    @Override
    public void updateEvent(EventDto event) {
//TODO
    }

    private Event convertDtoToEvent(EventDto dto) {
        EventType type;
        try {
            type = EventType.valueOf(dto.getType().toUpperCase());
        } catch (Exception e) {
            String errorMessage = "Wrong event type: '" + dto.getType() + "'";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        return new Event(dto.getId(),
                dto.getTitle(),
                dto.getText(),
                dto.isHasFeedback(),
                dto.getEventTime(),
                dto.isGroupEvent(),
                dto.isStudentEvent(),
                type);
    }

    private EventDto convertEventToEventDto(Event event) {
        return EventDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .text(event.getText())
                .hasFeedback(event.isHasFeedback())
                .isGroupEvent(event.isGroupEvent())
                .isStudentEvent(event.isStudentEvent())
                .type(event.getType().name())
                .eventTime(event.getEventTime())
                .build();
    }

    private void validateEvent(EventDto event) {
        if (event.getType().equals(EventType.EVENT.name())) {
            event.setEventTime(LocalDateTime.now());
        } else if (event.getEventTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Event with type not 'INFO' must be with eventTime");
        }
        if (event.isGroupEvent() && event.getGroupsIds().size() == 0) {
            throw new IllegalArgumentException("Event has 'for group' flag and doesn't have groups ids");
        }
        if (event.isStudentEvent() && event.getStudentsIds().size() == 0) {
            throw new IllegalArgumentException("Event has 'for student' flag and doesn't have groups ids");
        }
    }
}
