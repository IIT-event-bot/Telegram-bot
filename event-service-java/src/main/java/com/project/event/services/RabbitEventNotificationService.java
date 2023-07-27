package com.project.event.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.event.models.Event;
import com.project.event.models.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
            sendNotification(
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
    public void sendNotification(long chatId, String title, String text, NotificationType type, LocalDateTime sendTime, long eventId) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> values = Map.of(
                "type", type.name(),
                "title", title,
                "text", text,
                "chat_id", String.valueOf(chatId),
                "send_time", sendTime.toString(),
                "event_id", String.valueOf(eventId)
        );
        try {
            var message = mapper.writeValueAsString(values);
            rabbitTemplate.convertAndSend("service.notification", "notification-routing-key", message);
            log.info("Event send to queue to " + chatId);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
