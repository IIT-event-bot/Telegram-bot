package com.project.event.services;

import com.project.event.models.EventType;

import java.time.LocalDateTime;

public interface TelegramNotificationService {
    void sendNotification(long chatId, String title, String text, EventType type, LocalDateTime time, long eventId);
}
