package com.project.userService.services.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.userService.config.RabbitConfig;
import com.project.userService.models.RabbitMessage;
import com.project.userService.models.Statement;
import com.project.userService.models.User;
import com.project.userService.services.group.GroupService;
import com.project.userService.services.notification.TelegramNotificationService;
import com.project.userService.services.statement.StatementService;
import com.project.userService.services.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@EnableRabbit
@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitReceiver {
    private final UserService userService;
    private final StatementService statementService;
    private final GroupService groupService;
    private final TelegramNotificationService notificationService;

    @RabbitListener(queues = RabbitConfig.USER_SERVICE_QUEUE)
    public void userServiceQueueReceiver(Message message) {
        ObjectMapper mapper = new ObjectMapper();
        RabbitMessage rabbitMessage;
        try {
            rabbitMessage = mapper.readValue(message.getBody(), RabbitMessage.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return;
        }

        try {
            handleRabbitMessage(rabbitMessage);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            long id = Long.parseLong(rabbitMessage.body().get("id").toString());
            notificationService.sendNotification(id, "Ошибка при добавлении", "Заявка уже рассматривается");
        } catch (NullPointerException e) {
            log.error(e.getMessage());
            long id = Long.parseLong(rabbitMessage.body().get("id").toString());
            notificationService.sendNotification(id, "Ошибка при добавлении", "Группы с таким названием не существует");
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
        var group = groupService.getGroupsLikeTitle((String) body.get("groupName")).get(0);

        var statement = new Statement();
        statement.setGroupId(group.getId());
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
