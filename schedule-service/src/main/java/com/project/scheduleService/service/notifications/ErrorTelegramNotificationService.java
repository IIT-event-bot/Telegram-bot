package com.project.scheduleService.service.notifications;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ErrorTelegramNotificationService implements TelegramNotificationService {
    private final RabbitTemplate rabbitTemplate;
    @Value("${rabbit.notification-service.exchange}")
    private String notificationServiceExchange;
    @Value("${rabbit.notification-service.routingKey}")
    private String notificationServiceRoutingKey;

    @Override
    public void sendNotification(long chatId, String title, String text, LocalDateTime sendTime) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> values = Map.of(
                "type", "SYS_INFO",
                "title", title,
                "chatId", chatId,
                "text", text);
        try {
            var message = mapper.writeValueAsString(values);
            rabbitTemplate.convertAndSend(notificationServiceExchange, notificationServiceRoutingKey, message);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
