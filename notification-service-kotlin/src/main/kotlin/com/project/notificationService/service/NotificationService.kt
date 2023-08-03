package com.project.notificationService.service

import com.project.notificationService.models.Notification

interface NotificationService {
    fun saveNotification(notification: Notification)

    fun getNotificationOnTime(): List<Notification>

    fun sendNotification(notification: Notification)
}