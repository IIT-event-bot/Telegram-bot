package com.project.notificationService.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.project.notificationService.config.RabbitConfig
import com.project.notificationService.models.Notification
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service


@Service
@EnableRabbit
class RabbitNotificationServiceImpl(
    private val service: NotificationService,
    private val mapper: ObjectMapper
) {
    @RabbitListener(queues = [RabbitConfig.NOTIFICATION_SERVICE_QUEUE])
    fun receiveNotification(message: Message) {
        val body = String(message.body).removePrefix("\"").removeSuffix("\"").replace("\\", "")
        try {
            val notification: Notification = mapper.readValue<Notification>(body)
            service.saveNotification(notification)
        } catch (e: Exception) {
            log.error(e.message)
        }
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }
}