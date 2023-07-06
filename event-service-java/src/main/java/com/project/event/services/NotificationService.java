package com.project.event.services;

import com.project.event.models.EventType;

public interface NotificationService {
    <T> void sendNotification(String title, T message, EventType type);
}
