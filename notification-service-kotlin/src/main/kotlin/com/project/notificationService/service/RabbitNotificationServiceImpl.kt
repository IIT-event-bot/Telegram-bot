package com.project.notificationService.service

import com.project.notificationService.config.RabbitConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

@Service
@EnableRabbit
class RabbitNotificationServiceImpl(private val rabbitTemplate: RabbitTemplate) {
    @RabbitListener(queues = [RabbitConfig.NOTIFICATION_SERVICE_QUEUE])
    fun receiveNotification(message: Message) {

    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }
}