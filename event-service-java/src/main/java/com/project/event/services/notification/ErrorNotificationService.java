package com.project.event.services.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ErrorNotificationService implements TelegramNotificationService {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void sendNotification(long chatId, String title, String text, LocalDateTime sendTime) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, ?> values = Map.of(
                "title", title,
                "chat_id", chatId,
                "text", text,
                "send_time", sendTime.toString(),
                "type", "SYS_INFO"
        );
        try {
            String message = mapper.writeValueAsString(values);
            rabbitTemplate.convertAndSend("service.notification", "notification-routing-key", message);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
