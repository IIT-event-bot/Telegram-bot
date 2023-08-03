package com.project.notificationService.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.project.notificationService.NotificationRepository
import com.project.notificationService.models.Notification
import com.project.notificationService.models.NotificationType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneId

@Service
class NotificationServiceImpl(
    private val repository: NotificationRepository,
    private val notificationQueue: NotificationQueueService,
    private val rabbitTemplate: RabbitTemplate,
    private val converter: NotificationDtoConverter
) : NotificationService {
    @Transactional
    override fun saveNotification(notification: Notification) {
        if (notification.type == NotificationType.INFO || notification.type == NotificationType.SYS_INFO) {
            notification.sendTime = LocalDateTime.now(ZoneId.of("Asia/Yekaterinburg"))
            val savedNotification = repository.save(notification)
            sendNotification(savedNotification)
            return
        }
        if ((notification.type == NotificationType.EVENT || notification.type == NotificationType.FEEDBACK)
            && notification.sendTime == null
        ) {
            throw IllegalArgumentException("Notification with type not 'INFO' or 'SYS_INFO' must be with 'sendTime' parameter")
        }
        if ((notification.type == NotificationType.EVENT || notification.type == NotificationType.FEEDBACK)
            && notification.eventId == null
        ) {
            throw IllegalArgumentException("Notification with type 'EVENT' or 'FEEDBACK' must be with 'eventId' parameter")
        }
        val inHour = LocalDateTime.now(ZoneId.of("Asia/Yekaterinburg")).plusHours(1)
        if (notification.sendTime!!.isBefore(inHour)) {
            notificationQueue.pushNotificationToQueue(notification)
        }
        repository.save(notification)
    }

    override fun getNotificationBeforeTime(time: LocalDateTime): List<Notification> {
        return repository.getAllBySendTimeBetween(LocalDateTime.now(ZoneId.of("Asia/Yekaterinburg")), time)
    }

    override fun sendNotification(notification: Notification) {
        val mapper: ObjectMapper = jacksonObjectMapper()
        try {
            val dto = converter.map(notification)
            rabbitTemplate.convertAndSend(
                "service.telegram",
                "telegram-routing-key",
                mapper.writeValueAsString(dto)
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