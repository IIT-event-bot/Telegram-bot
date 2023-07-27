package com.project.scheduleService.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String SCHEDULE_SERVICE_QUEUE = "schedule-service";
    public static final String EXCHANGE = "service.schedule";
    public static final String ROUTING_KEY = "schedule-routing-key";

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Bean
    public CachingConnectionFactory connectionFactory() {
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
    public Queue scheduleServiceQueue() {
        return new Queue(SCHEDULE_SERVICE_QUEUE, true, false, false);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding binding(TopicExchange exchange) {
        return BindingBuilder.bind(scheduleServiceQueue()).to(exchange).with(ROUTING_KEY);
    }
}
