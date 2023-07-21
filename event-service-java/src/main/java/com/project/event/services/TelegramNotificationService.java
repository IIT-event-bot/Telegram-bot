package com.project.event.services;

import com.project.event.models.NotificationType;

import java.time.LocalDateTime;

public interface TelegramNotificationService extends NotificationService {
    void sendNotification(long chatId, String title, String text, NotificationType type, LocalDateTime sendTime, long eventId);
}
