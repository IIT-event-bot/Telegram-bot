package com.project.userService.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String USER_SERVICE_QUEUE = "user-service";
    @Value("${rabbit.user-service.exchange}")
    private String EXCHANGE;
    @Value("${rabbit.user-service.routingKey}")
    private String ROUTING_KEY;

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Bean
    public ConnectionFactory connectionFactory() {
        return new CachingConnectionFactory(host);
    }

    @Bean
    public AmqpAdmin admin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        var template = new RabbitTemplate(connectionFactory());
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }

    @Bean
    public Queue userServiceQueue() {
        return new Queue(USER_SERVICE_QUEUE, true, false, false);
    }

    @Bean
    public Exchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding binding(Exchange exchange) {
        return BindingBuilder.bind(userServiceQueue()).to(exchange).with(ROUTING_KEY).noargs();
    }
}
