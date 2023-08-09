package com.project.scheduleService.service.notifications;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.scheduleService.models.Lesson;
import com.project.scheduleService.models.dto.GroupDto;
import com.project.scheduleService.models.dto.UserDto;
import com.project.scheduleService.service.students.GroupService;
import com.project.scheduleService.service.students.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleTelegramNotificationServiceImpl implements ScheduleTelegramNotificationService {
    private final RabbitTemplate rabbitTemplate;
    private final GroupService groupService;
    private final StudentService studentService;

    @Value("${rabbit.notification-service.exchange}")
    private String notificationServiceExchange;
    @Value("${rabbit.notification-service.routingKey}")
    private String notificationServiceRoutingKey;

    @Override
    public void sendSchedule(List<Lesson> lessons) {
        if (lessons.size() == 0) {
            return;
        }
        GroupDto group = groupService.getGroupById(lessons.get(0).getGroupId());
        List<UserDto> users = studentService.getUserIdByGroupId(group.id());
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Yekaterinburg"));
        for (UserDto user : users) {
            for (Lesson lesson : lessons) {
                sendLesson(user, lesson, today);
            }
        }
    }

    private void sendLesson(UserDto user, Lesson lesson, LocalDate sendDate) {
        LocalDateTime sendTime = LocalDateTime.of(sendDate, lesson.getTimeStart());
        sendNotification(user.id(), lesson, sendTime);
        for (Long userId : lesson.getLocalUsers()) {
            sendNotification(userId, lesson, sendTime);
        }
    }

    private void sendNotification(Long userId, Lesson lesson, LocalDateTime sendTime) {
        this.sendNotification(
                userId,
                "Скоро начнется пара",
                "<b>Пара</b>: " + lesson.getTitle() + "\n" +
                        "<b>Преподаватель:</b> " + lesson.getTeacher() + "\n" +
                        "<b>Начало пары:</b> " + lesson.getTimeStart() + "\n" +
                        "<b>Конец пары:</b> " + lesson.getTimeEnd() + "\n" +
                        "<b>Аудитория</b> : " + lesson.getAuditorium(),
                sendTime);
    }

    @Override
    public void sendNotification(long chatId,
                                 String title,
                                 String text,
                                 LocalDateTime sendTime) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, ?> values = Map.of(
                "type", "SCHEDULE",
                "title", title,
                "chatId", chatId,
                "text", text,
                "sendTime", sendTime.toString());
        try {
            var message = mapper.writeValueAsString(values);
            rabbitTemplate.convertAndSend(notificationServiceExchange, notificationServiceRoutingKey, message);
            log.info("Message sending to queue to chat " + chatId);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
