package com.project.event.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.event.models.Event;
import com.project.event.models.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitEventNotificationServiceImpl implements EventNotificationService {
    private final RabbitTemplate rabbitTemplate;
    private final StudentService studentService;

    @Override
    public void sendEvent(Event event) {
        List<Long> chatIds = new ArrayList<>();
        if (event.isGroupEvent()) {
            for (var groupId : event.getGroups()) {
                chatIds.addAll(studentService.getStudentChatIdByGroupId(groupId));
            }
        }
        if (event.isStudentEvent()) {
            for (var studentId : event.getStudents()) {
                chatIds.add(studentService.getStudentChatIdById(studentId));
            }
        }
        for (var student : chatIds) {
            sendNotification(event.getTitle(),
                    Map.of("chatId", student,
                            "text", event.getText(),
                            "send_time", event.getEventTime().toString()),
                    event.getType());
        }
    }

    @Override
    public void sendEvents(List<Event> events) {
        for (var event : events) {
            log.info(event.getId() + " " + event.getTitle() + " at " + event.getEventTime());
            sendEvent(event);
        }
    }

    @Override
    public <T> void sendNotification(String title, T object, EventType type) {
        ObjectMapper mapper = new ObjectMapper();
        var values = mapper.convertValue(object, new TypeReference<Map<String, Object>>() {
        });
        values.put("type", type.name());
        values.put("title", title);
        try {
            var message = mapper.writeValueAsString(values);
            rabbitTemplate.convertAndSend("service.notification", "notification-routing-key", message);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
