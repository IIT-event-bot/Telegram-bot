package com.project.event.services;

import com.project.event.models.Event;

import java.util.List;

public interface TelegramEventNotificationService extends TelegramNotificationService {
    void sendEvent(Event event);

    void sendEvents(List<Event> events);
}
