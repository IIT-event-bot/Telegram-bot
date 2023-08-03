package com.project.notificationService.service

import com.project.notificationService.models.Notification
import com.project.notificationService.models.NotificationDto
import com.project.notificationService.models.NotificationType
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class NotificationDtoMapper : NotificationDtoConverter {
    override fun map(notification: Notification): NotificationDto {
        return NotificationDto(
            id = notification.id,
            type = notification.type.name,
            chatId = notification.chatId,
            text = notification.text,
            title = notification.title,
            sendTime = notification.sendTime!!.toEpochSecond(ZoneOffset.of("+5")),
            eventId = notification.eventId
        )
    }

    override fun map(dto: NotificationDto): Notification {
        return Notification(
            id = dto.id,
            type = NotificationType.valueOf(dto.type),
            chatId = dto.chatId,
            text = dto.text,
            title = dto.title,
            sendTime = LocalDateTime.ofEpochSecond(dto.sendTime, 0, ZoneOffset.of("+5")),
            eventId = dto.eventId
        )
    }
}