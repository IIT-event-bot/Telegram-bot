package com.project.userService.utils;

import com.project.userService.services.notification.TelegramNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class ShutdownAppHandler {
    private final TelegramNotificationService errorNotificationService;
    @Value("${admin.chat.id}")
    private long adminChatId;

    public ShutdownAppHandler(TelegramNotificationService errorNotificationService) {
        this.errorNotificationService = errorNotificationService;
    }

    @EventListener(ContextClosedEvent.class)
    @Transactional
    public void shutdownHandle() {
        errorNotificationService.sendNotification(
                adminChatId,
                "Выключение сервиса",
                "Сервис пользователей выключен"
        );
        log.info("User service shutdown");
    }
}
