package com.project.userService.services.notification;

public interface TelegramNotificationService extends NotificationService {
    void sendNotification(long chatId, String title, String text);
}
