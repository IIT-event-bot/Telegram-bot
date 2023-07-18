package com.project.userService.services.notification;

public interface TelegramNotificationService {
    void sendNotification(long chatId, String title, String text);
}
