package com.project.event.services;

import com.project.event.models.Event;
import com.project.event.models.EventType;

import java.util.List;

public interface EventNotificationService {
    void sendEvent(Event event);

    void sendEvents(List<Event> events);

    <T> void sendNotification(String title, T message, EventType type);
}
