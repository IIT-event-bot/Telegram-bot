package com.project.userService.services.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.userService.config.RabbitConfig;
import com.project.userService.models.RabbitMessage;
import com.project.userService.models.Statement;
import com.project.userService.models.User;
import com.project.userService.services.GroupService;
import com.project.userService.services.StatementService;
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
    private final StatementService statementService;
    private final GroupService groupService;

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
            case ADD_STATEMENT -> saveStatementFromQueue(message.body());
        }
    }

    private void saveStatementFromQueue(Map<String, Object> body) {
        var user = userService.getUserById(Long.parseLong(body.get("id").toString()));

        var statement = new Statement();
        statement.setGroupName((String) body.get("groupName"));
        statement.setUserId(user.getId());
        statement.setName((String) body.get("name"));
        statement.setSurname((String) body.get("surname"));
        statement.setPatronymic((String) body.get("patronymic"));

        statementService.saveStatement(statement);
    }

    private void saveUserFromQueue(Map<String, Object> body) {
        var user = new User();
        user.setId(Long.parseLong(body.get("id").toString()));
        user.setUsername((String) body.get("username"));

        userService.saveUser(user);
    }
}
