package com.project.notificationService.service

import com.project.notificationService.models.Notification
import com.project.notificationService.models.NotificationDto

interface NotificationDtoConverter {
    fun map(notification: Notification): NotificationDto
    fun map(dto: NotificationDto): Notification
}