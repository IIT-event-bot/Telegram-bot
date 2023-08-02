package com.project.userService.services.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitTelegramNotificationService implements TelegramNotificationService {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void sendNotification(long chatId, String title, String text) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> values = Map.of(
                "type", "SYS_INFO",
                "title", title,
                "chat_id", String.valueOf(chatId),
                "text", text);
        try {
            var message = mapper.writeValueAsString(values);
            rabbitTemplate.convertAndSend("service.notification", "notification-routing-key", message);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}