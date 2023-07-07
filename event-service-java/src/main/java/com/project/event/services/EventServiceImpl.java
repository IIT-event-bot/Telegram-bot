package com.project.event.services;

import com.project.event.models.Event;
import com.project.event.models.EventType;
import com.project.event.repositories.EventRepository;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
public class EventServiceImpl implements EventService {
    private final EventRepository repository;
    private final NotificationService notificationService;
    @Value("${grpc.userservice.host}")
    private String userServiceHost;

    @Override
    public List<Event> getAllEvents(String date, String title, Long groupId) {
        if (date != null && title != null && groupId != null) {
            var eventDate = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            return repository.getEventsByFilter(title, eventDate, groupId);
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
        return result;
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
        repository.save(event);
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
        repository.save(event);
    }

    private void validateStudents(List<Long> students) {
        for (var studentId : students) {
            checkStudentExists(studentId);
        }
    }

    private void validateGroups(List<Long> groups) {
        for (var group : groups) {
            checkGroupExists(group);
        }
    }

    private void checkGroupExists(long groupId) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(userServiceHost, 8100)
                .usePlaintext()
                .build();

        var stub = com.project.groupService.GroupServiceGrpc.newBlockingStub(channel);

        var request = com.project.groupService.GroupServiceOuterClass.GroupRequest
                .newBuilder()
                .setGroupId(groupId)
                .build();
        try {
            var response = stub.getGroupByGroupId(request);
        } catch (io.grpc.StatusRuntimeException e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException("Group with id " + groupId + " does not exist");
        }
    }

    private void checkStudentExists(Long userId) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(userServiceHost, 8100)
                .usePlaintext()
                .build();

        var stub = com.project.studentService.StudentServiceGrpc.newBlockingStub(channel);

        var request = com.project.studentService.StudentServiceOuterClass.StudentRequest
                .newBuilder()
                .setStudentId(userId)
                .build();
        try {
            var response = stub.getStudentById(request);
        } catch (io.grpc.StatusRuntimeException e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException("Student with id " + userId + " does not exist");
        }
    }

    private void validateEvent(Event event) {
        if (event.getType().equals(EventType.INFO)) {
            event.setEventTime(LocalDateTime.now());
        } else if (event.getEventTime() == null) {
            throw new IllegalArgumentException("Event with type not 'INFO' must be with eventTime");
        } else if (event.getEventTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Event time is earlier than now");
        }
        if (event.isRepeat() && event.getRepeatTime().size() == 0) {
            throw new IllegalArgumentException("Event has set flag 'isGroupEvent' and doesn't have repeat time");
        }
        if (event.isRepeat()) {
            var now = LocalDateTime.now();
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

    @Scheduled(cron = "0 0 0 * * ?")
    public void sendEventsToday() {
        var now = LocalDateTime.now();
        var dayEnd = now.plusDays(1);
        var eventsToday = repository.getEventsByEventTimeBetween(now, dayEnd);
        sendEvents(eventsToday);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void sendRepeatEvent() {
        var now = LocalDateTime.now();
        var dayEnd = now.plusDays(1);
        var eventsRepeat = repository.getEventsByRepeatTimeBetween(now, dayEnd);
        var allRepeats = getAllRepeatEvents(eventsRepeat);
        sendEvents(allRepeats);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void sendFeedback() {
        var now = LocalDateTime.now();
        var dayAgo = now.minusDays(1);
        var feedback = repository.getEventByFeedbackTime(dayAgo, now);
        for (var event : feedback) {
            var feedbackTime = event.getEventTime().plusDays(1);
            event.setEventTime(feedbackTime);
            var eventText = "Вчера прошло событие '" + event.getTitle() + "', оставьте, пожалуйста обратную связь по этому событию";
            event.setText(eventText);
            event.setType(EventType.FEEDBACK);
        }
        sendEvents(feedback);
    }

    private List<Event> getAllRepeatEvents(List<Event> events) {
        var now = LocalDateTime.now();
        var dayEnd = now.plusDays(1);
        List<Event> allRepeats = new ArrayList<>();
        for (Event event : events) {
            for (var repeatTime : event.getRepeatTime()) {
                if (repeatTime.isAfter(now) && repeatTime.isBefore(dayEnd)) {
                    var repeatEvent = new Event();
                    repeatEvent.setId(event.getId());
                    repeatEvent.setStudentEvent(event.isStudentEvent());
                    repeatEvent.setStudents(event.getStudents());
                    repeatEvent.setGroupEvent(event.isGroupEvent());
                    repeatEvent.setGroups(event.getGroups());
                    repeatEvent.setEventTime(repeatTime);
                    repeatEvent.setTitle(event.getTitle());
                    repeatEvent.setText(event.getText());

                    allRepeats.add(repeatEvent);
                }
            }
        }
        return allRepeats;
    }


    private void sendEvents(List<Event> events) {
        for (var event : events) {
            log.info(event.getId() + " " + event.getTitle() + " at " + event.getEventTime());
            sendEvent(event);
        }
    }

    private void sendEvent(Event event) {
        List<Long> chatIds = new ArrayList<>();
        if (event.isGroupEvent()) {
            for (var groupId : event.getGroups()) {
                chatIds.addAll(getGroupStudentChatId(groupId));
            }
        }
        if (event.isStudentEvent()) {
            for (var studentId : event.getStudents()) {
                chatIds.add(getStudentChatId(studentId));
            }
        }
        for (var student : chatIds) {
            notificationService.sendNotification(event.getTitle(),
                    Map.of("chatId", student,
                            "text", event.getText(),
                            "send_time", event.getEventTime().toString()),
                    event.getType());
        }
    }

    private Long getStudentChatId(Long studentId) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(userServiceHost, 8100)
                .usePlaintext()
                .build();

        var stub = com.project.studentService.StudentServiceGrpc.newBlockingStub(channel);

        var request = com.project.studentService.StudentServiceOuterClass.StudentRequest
                .newBuilder()
                .setStudentId(studentId)
                .build();
        try {
            var response = stub.getUserByStudentId(request);
            return response.getChatId();
        } catch (io.grpc.StatusRuntimeException e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException("Student with id " + studentId + " does not exist");
        }
    }

    private List<Long> getGroupStudentChatId(Long groupId) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(userServiceHost, 8100)
                .usePlaintext()
                .build();

        var stub = com.project.studentService.StudentServiceGrpc.newBlockingStub(channel);

        var request = com.project.studentService.StudentServiceOuterClass.GroupRequest
                .newBuilder()
                .setGroupId(groupId)
                .build();
        List<Long> ids = new ArrayList<>();
        try {
            var response = stub.getStudentsChatIdByGroupId(request);
            while (response.hasNext()) {
                var group = response.next();
                ids.add(group.getChatId());
            }
        } catch (io.grpc.StatusRuntimeException e) {
            log.error(e.getMessage());
        }
        return ids;
    }
}
