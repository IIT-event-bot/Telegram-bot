package com.project.scheduleService.service.util;

import com.project.scheduleService.service.notifications.TelegramNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Slf4j
public class ShutdownAppHandler {
    private final TelegramNotificationService errorNotificationService;
    @Value("${admin.chat.id}")
    private long adminChatId;

    public ShutdownAppHandler(@Qualifier("errorTelegramNotificationService") TelegramNotificationService errorNotificationService) {
        this.errorNotificationService = errorNotificationService;
    }

    @EventListener(ContextClosedEvent.class)
    @Transactional
    public void shutdownHandle() {
        errorNotificationService.sendNotification(
                adminChatId,
                "Выключение сервиса",
                "Сервис расписания выключен",
                LocalDateTime.now()
        );
        log.info("Schedule service shutdown");
    }
}
