package com.project.event.services;

import com.project.event.models.Event;
import com.project.event.models.EventCheck;
import com.project.event.models.EventDto;
import com.project.event.models.NotificationType;
import com.project.event.repositories.CheckedStudentRepository;
import com.project.event.repositories.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository repository;
    private final TelegramEventNotificationService notificationService;
    private final CheckedStudentRepository checkedStudentRepository;
    private final StudentService studentService;
    private final EventDtoMapper dtoMapper;

    @Override
    public List<EventDto> getAllEvents(String date, String title, Long groupId) {
        if (date == null && title == null && groupId == null) {
            return repository.findAll()
                    .stream()
                    .map(dtoMapper::convert)
                    .toList();
        }
        if (date != null && title != null && groupId != null) {
            var eventDate = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            return repository.getEventsByFilter(title, eventDate, groupId)
                    .stream()
                    .map(dtoMapper::convert)
                    .toList();
        }
        List<Event> result = new ArrayList<>();
        if (title != null) {
            result.addAll(repository.getEventsByTitleLikeIgnoreCase(title));
        }
        if (date != null) {
            result = filterEventsByDate(result, date);
        }
        if (groupId != null) {
            result = filterEventsByGroupId(result, groupId);
        }
        return result.stream()
                .map(dtoMapper::convert)
                .toList();
    }

    private List<Event> filterEventsByGroupId(List<Event> events, Long groupId) {
        if (events.size() == 0) {
            events.addAll(repository.getEventsByGroupId(groupId));
        } else {
            events = events.stream().filter(x -> x.getGroups().contains(groupId)).collect(Collectors.toList());
        }
        return events;
    }

    private List<Event> filterEventsByDate(List<Event> events, String date) {
        var eventDate = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        if (events.size() == 0) {
            events.addAll(repository.getEventsByEventDate(eventDate));
        } else {
            events = events.stream().filter(x -> x.getEventTime().getDayOfMonth() == eventDate.getDayOfMonth()
                            && x.getEventTime().getMonth() == eventDate.getMonth()
                            && x.getEventTime().getYear() == eventDate.getYear())
                    .collect(Collectors.toList());
        }
        return events;
    }

    @Override
    public Event getEventById(long id) {
        return repository.getEventById(id);
    }

    @Override
    @Transactional
    public void createEvent(Event event) {
        validateEvent(event);
        var savedEvent = repository.save(event);
        sendEventIfTimeToday(savedEvent);
    }

    private void sendEventIfTimeToday(Event event) {
        if (event.getType() == NotificationType.INFO) {
            notificationService.sendEvent(event);
            return;
        }
        if (event.getType() == NotificationType.EVENT
                && event.getEventTime()
                .isBefore(ZonedDateTime.now(ZoneId.of("Asia/Yekaterinburg"))
                        .toLocalDateTime()
                        .plusDays(1))) {
            notificationService.sendEvent(event);
            return;
        }
        if (!event.isRepeat()) {
            return;
        }
        var todayTime = ZonedDateTime.now(ZoneId.of("Asia/Yekaterinburg")).toLocalDateTime().plusDays(1);
        for (var repeat : event.getRepeatTime()) {
            if (!repeat.isBefore(todayTime)) {
                continue;
            }
            event.setEventTime(repeat);
            notificationService.sendEvent(event);
        }
    }

    @Override
    public void deleteEventById(long eventId) {
        var event = repository.getEventById(eventId);
        repository.delete(event);
    }

    @Override
    public void updateEvent(Event event) {
        validateEvent(event);
        var savedEvent = repository.getEventById(event.getId());
        if (savedEvent == null) {
            throw new IllegalArgumentException("Event with id " + event.getId() + " does not exist");
        }
        savedEvent = repository.save(event);
        sendEventIfTimeToday(savedEvent);
    }

    @Override
    public List<Long> getEventCheckedStudentId(long eventId) {
        return checkedStudentRepository.getEventChecksByEventId(eventId).stream().map(EventCheck::getStudentsId).toList();
    }

    private void validateStudents(List<Long> students) {
        for (var studentId : students) {
            studentService.getStudentById(studentId);
        }
    }

    private void validateGroups(List<Long> groups) {
        for (var group : groups) {
            studentService.getGroupById(group);
        }
    }

    private void validateEvent(Event event) {
        if (event.getType().equals(NotificationType.INFO)) {
            event.setEventTime(ZonedDateTime.now(ZoneId.of("Asia/Yekaterinburg")).toLocalDateTime());
        } else if (event.getEventTime() == null) {
            throw new IllegalArgumentException("Event with type not 'INFO' must be with eventTime");
        } else if (event.getEventTime().isBefore(ZonedDateTime.now(ZoneId.of("Asia/Yekaterinburg")).toLocalDateTime())) {
            throw new IllegalArgumentException("Event time is earlier than now");
        }
        if (event.isRepeat() && event.getRepeatTime().size() == 0) {
            throw new IllegalArgumentException("Event has set flag 'isGroupEvent' and doesn't have repeat time");
        }
        if (event.isRepeat()) {
            var now = ZonedDateTime.now(ZoneId.of("Asia/Yekaterinburg")).toLocalDateTime();
            for (var repeatTime : event.getRepeatTime()) {
                if (repeatTime.isBefore(now)) {
                    throw new IllegalArgumentException("Event repeat time is earlier than now");
                }
            }
        }
        if (event.isGroupEvent() && event.getGroups().size() == 0) {
            throw new IllegalArgumentException("Event has set flag 'isGroupEvent' and doesn't have groups ids");
        }
        if (event.isGroupEvent()) {
            validateGroups(event.getGroups());
        }
        if (event.isStudentEvent() && event.getStudents().size() == 0) {
            throw new IllegalArgumentException("Event has set flag 'isStudentEvent' and doesn't have students ids");
        }
        if (event.isStudentEvent()) {
            validateStudents(event.getStudents());
        }
        if (!event.isStudentEvent() && !event.isGroupEvent()) {
            throw new IllegalArgumentException("Event must be for students or for groups");
        }
    }
}
