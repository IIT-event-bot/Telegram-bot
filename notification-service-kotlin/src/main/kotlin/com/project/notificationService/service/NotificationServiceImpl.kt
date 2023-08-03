package com.project.notificationService.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.project.notificationService.NotificationRepository
import com.project.notificationService.models.Notification
import com.project.notificationService.models.NotificationType
import jakarta.transaction.Transactional
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneId

@Service
@Slf4j
@RequiredArgsConstructor
class NotificationServiceImpl(
    private val repository: NotificationRepository,
    private val notificationQueue: NotificationQueueService,
    private val rabbitTemplate: RabbitTemplate
) : NotificationService {
    @Transactional
    override fun saveNotification(notification: Notification) {
        if (notification.type == NotificationType.INFO || notification.type == NotificationType.SYS_INFO) {
            notification.sendTime = LocalDateTime.now(ZoneId.of("Asia/Yekaterinburg"))
            sendNotification(notification)
        } else if (notification.sendTime!!.isBefore(LocalDateTime.now(ZoneId.of("Asia/Yekaterinburg")).plusHours(1))) {
            notificationQueue.pushNotificationToQueue(notification)
        }
        repository.save(notification)
    }

    override fun getNotificationOnTime(): List<Notification> {
        return notificationQueue.getNowNotification()
    }

    override fun sendNotification(notification: Notification) {
        val mapper: ObjectMapper = jacksonObjectMapper()
        try {
            rabbitTemplate.convertAndSend(
                "service.telegram",
                "telegram-routing-key",
                mapper.writeValueAsString(notification)
            )
            log.info("Send notification [ type: ${notification.type.name}, user: ${notification.chatId} ]")
        } catch (e: Exception) {
            log.error(e.message)
        }
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }
}