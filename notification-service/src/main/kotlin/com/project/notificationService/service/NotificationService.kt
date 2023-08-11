package com.project.notificationService.service

import com.project.notificationService.models.Notification
import java.time.LocalDateTime

interface NotificationService {
    fun saveNotification(notification: Notification)

    fun getNotificationBeforeTime(time: LocalDateTime): List<Notification>

    fun sendNotification(notification: Notification)

    fun getNotSendingBeforeNotification(): List<Notification>
}