package com.project.userService.services.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.userService.config.RabbitConfig;
import com.project.userService.models.RabbitMessage;
import com.project.userService.models.User;
import com.project.userService.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@EnableRabbit
@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitReceiver {
    private final UserService userService;

    @RabbitListener(queues = RabbitConfig.USER_SERVICE_QUEUE)
    public void userServiceQueue(Message message) {
        ObjectMapper mapper = new ObjectMapper();
        RabbitMessage rabbitMessage;
        try {
            rabbitMessage = mapper.readValue(message.getBody(), RabbitMessage.class);
            handleRabbitMessage(rabbitMessage);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void handleRabbitMessage(RabbitMessage message) {
        switch (message.method()) {
            case ADD_USER -> saveUserFromQueue(message.body());
            case ADD_STATEMENT -> throw new IllegalArgumentException("Unimplemented method");
        }
    }

    private void saveUserFromQueue(Map<String, Object> body) {
        var user = new User();
        user.setChatId(Long.parseLong(body.get("chatId").toString()));
        user.setUsername((String) body.get("username"));

        userService.saveUser(user);
    }
}
