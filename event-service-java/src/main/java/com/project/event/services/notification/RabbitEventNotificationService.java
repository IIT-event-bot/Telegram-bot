package com.project.event.services.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.event.models.Event;
import com.project.event.models.utils.NotificationType;
import com.project.event.services.student.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitEventNotificationService implements TelegramEventNotificationService {
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
                chatIds.add(studentService.getChatIdByStudentId(studentId));
            }
        }
        for (var chatId : chatIds) {
            sendEventNotification(
                    chatId,
                    event.getTitle(),
                    event.getText(),
                    event.getType(),
                    event.getEventTime(),
                    event.getId()
            );
        }
    }

    @Override
    public void sendEvents(List<Event> events) {
        for (var event : events) {
            sendEvent(event);
        }
    }

    @Override
    public void sendEventNotification(long chatId,
                                      String title,
                                      String text,
                                      NotificationType type,
                                      LocalDateTime sendTime,
                                      long eventId) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, ?> values = Map.of(
                "type", type.name(),
                "text", text,
                "event_id", String.valueOf(eventId)
        );
        try {
            sendNotification(chatId, title, mapper.writeValueAsString(values), sendTime);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void sendNotification(long chatId,
                                 String title,
                                 String text,
                                 LocalDateTime sendTime) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> values = new HashMap<>();
        values.put("chat_id", chatId);
        values.put("title", title);
        values.put("send_time", sendTime.toString());

        try {
            Map<String, Object> mapBody = mapper.readValue(text, new TypeReference<>() {
            });
            values.putAll(mapBody);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            final String errorMessage = "Body '" + text + "' doesn't serializable to map";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        try {
            var message = mapper.writeValueAsString(values);
            rabbitTemplate.convertAndSend("service.notification", "notification-routing-key", message);
            log.info("Event send to queue to " + chatId);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
