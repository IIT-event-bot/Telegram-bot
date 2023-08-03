package com.project.notificationService.service

import com.project.notificationService.models.Notification
import java.time.LocalTime

interface NotificationService {
    fun saveNotification(notification: Notification)

    fun getNotificationBeforeTime(time: LocalTime): List<Notification>
}