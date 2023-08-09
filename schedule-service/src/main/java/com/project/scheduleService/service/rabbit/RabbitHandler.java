package com.project.scheduleService.service.rabbit;

import com.project.scheduleService.config.RabbitConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@EnableRabbit
@Slf4j
public class RabbitHandler {
    @RabbitListener(queues = RabbitConfig.SCHEDULE_SERVICE_QUEUE)
    public void handleMessageFromQueue(Message message){
        log.info("Get message from queue: '" + new String(message.getBody()) + "'");
    }
}
