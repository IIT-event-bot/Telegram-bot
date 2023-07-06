package com.project.event.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.event.models.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitNotificationServiceImpl implements NotificationService {
    private final RabbitTemplate rabbitTemplate;

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
