package com.project.notificationService.service

import com.project.notificationService.models.Notification
import com.project.notificationService.models.NotificationType
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class ShutdownAppHandler(
    private val notificationService: NotificationService
) {
    @Value("\${admin.chat.id}")
    private var adminChat: Long = 0

    @EventListener(ContextClosedEvent::class)
    @Transactional
    fun handleShutdownApp() {
        notificationService.sendNotification(
            Notification(
                id = 0L,
                type = NotificationType.SYS_INFO,
                chatId = adminChat,
                text = "Сервис уведомлений выключен",
                title = "Выключение сервиса",
                sendTime = LocalDateTime.now(),
                null,
                true
            )
        )
        logger.info("Notification service shutdown")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }
}