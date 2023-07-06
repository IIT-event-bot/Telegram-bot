package com.project.userService.services;

public interface NotificationService {
    <T> void sendNotification(String title, T message);
}
