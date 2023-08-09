package com.project.notificationService.service

import com.project.notificationService.models.Notification

interface NotificationQueueService {
    fun pushNotificationsToQueue(notifications: List<Notification>)

    fun pushNotificationToQueue(notification: Notification)

    fun clearQueue()

    fun getNowNotification(): List<Notification>
}