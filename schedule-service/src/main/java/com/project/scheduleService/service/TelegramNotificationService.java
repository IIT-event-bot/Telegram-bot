package com.project.scheduleService.service;

import java.time.LocalDateTime;

public interface TelegramNotificationService extends NotificationService {
    void sendNotification(long chatId, String title, String text, LocalDateTime sendTime);
}
