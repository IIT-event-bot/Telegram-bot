package com.project.notificationService.config

import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfig {
    @Value("\${spring.rabbitmq.host}")
    private lateinit var host: String
    @Value("\${rabbit.notification-service.exchange}")
    private lateinit var EXCHANGE: String

    @Value("\${rabbit.notification-service.routingKey}")
    private lateinit var ROUTING_KEY: String

    @Bean
    fun connectionFactory(): ConnectionFactory {
        return CachingConnectionFactory(host)
    }

    @Bean
    fun admin(): AmqpAdmin {
        return RabbitAdmin(connectionFactory())
    }

    @Bean
    fun rabbitTemplate(): RabbitTemplate {
        val template = RabbitTemplate(
            connectionFactory()
        )
        template.messageConverter = Jackson2JsonMessageConverter()
        return template
    }

    @Bean
    fun userServiceQueue(): Queue {
        return Queue(NOTIFICATION_SERVICE_QUEUE, true, false, false)
    }

    @Bean
    fun exchange(): TopicExchange {
        return TopicExchange(EXCHANGE)
    }

    @Bean
    fun binding(exchange: TopicExchange): Binding {
        return BindingBuilder.bind(userServiceQueue()).to(exchange).with(ROUTING_KEY)
    }

    companion object {
        const val NOTIFICATION_SERVICE_QUEUE = "send-notification"
    }
}