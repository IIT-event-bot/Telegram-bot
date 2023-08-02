package com.project.event.services.notification;

import com.project.event.models.Event;
import com.project.event.models.utils.NotificationType;

import java.time.LocalDateTime;
import java.util.List;

public interface TelegramEventNotificationService extends TelegramNotificationService {
    void sendEvent(Event event);

    void sendEvents(List<Event> events);

    void sendEventNotification(long chatId, String title, String text, NotificationType type, LocalDateTime sendTime, long eventId);
}
