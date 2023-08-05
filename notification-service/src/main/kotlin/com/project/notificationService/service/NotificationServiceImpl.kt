package com.project.notificationService.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.project.notificationService.models.Notification
import com.project.notificationService.models.NotificationType
import com.project.notificationService.repository.NotificationRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneId

@Service
class NotificationServiceImpl(
    private val repository: NotificationRepository,
    private val notificationQueue: NotificationQueueService,
    private val rabbitTemplate: RabbitTemplate,
    private val mapper: ObjectMapper
) : NotificationService {
    @Value("\${rabbit.telegram-bot.exchange}")
    private lateinit var tgBotExchange: String

    @Value("\${rabbit.telegram-bot.routingKey}")
    private lateinit var tgBotRoutingKey: String

    @Transactional
    override fun saveNotification(notification: Notification) {
        if (notification.type == NotificationType.INFO || notification.type == NotificationType.SYS_INFO) {
            notification.sendTime = LocalDateTime.now(ZoneId.of("Asia/Yekaterinburg"))
            val savedNotification = repository.save(notification)
            sendNotification(savedNotification)
            return
        }
        validateNotification(notification)
        val inHour = LocalDateTime.now(ZoneId.of("Asia/Yekaterinburg")).plusHours(1)
        if (notification.sendTime!!.isBefore(inHour)) {
            notificationQueue.pushNotificationToQueue(notification)
        }
        repository.save(notification)
    }

    private fun validateNotification(notification: Notification) {
        if (notification.type in arrayListOf(
                NotificationType.EVENT,
                NotificationType.SYS_INFO,
                NotificationType.SCHEDULE
            ) && notification.sendTime == null
        ) {
            throw IllegalArgumentException("Notification with type not 'INFO' or 'SYS_INFO' must be with 'sendTime' parameter")
        }
        if (notification.type in arrayListOf(
                NotificationType.EVENT,
                NotificationType.SYS_INFO
            )
            && notification.eventId == null
        ) {
            throw IllegalArgumentException("Notification with type 'EVENT' or 'FEEDBACK' must be with 'eventId' parameter")
        }
    }

    override fun getNotificationBeforeTime(time: LocalDateTime): List<Notification> {
        return repository.getAllBySendTimeBetween(LocalDateTime.now(ZoneId.of("Asia/Yekaterinburg")), time)
    }

    override fun sendNotification(notification: Notification) {
        try {
            rabbitTemplate.convertAndSend(
                tgBotExchange,
                tgBotRoutingKey,
                mapper.writeValueAsString(notification)
            )
            log.debug("Send notification [ type: ${notification.type.name}, user: ${notification.chatId} ]")
        } catch (e: Exception) {
            log.error(e.message)
        }
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }
}