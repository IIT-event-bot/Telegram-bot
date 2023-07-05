package com.project.event.services;

import com.project.event.models.Event;
import com.project.event.models.EventType;
import com.project.event.repositories.EventRepository;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
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
                .forTarget("localhost:8100")
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
                .forTarget("localhost:8100")
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
        } else if (event.getEventTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Event with type not 'INFO' must be with eventTime");
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
    }
}
