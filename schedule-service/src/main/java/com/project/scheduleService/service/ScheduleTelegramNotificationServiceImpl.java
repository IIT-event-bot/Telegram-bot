package com.project.scheduleService.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.scheduleService.models.Lesson;
import com.project.scheduleService.models.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
    private final UserService userService;

    @Override
    public void sendSchedule(List<Lesson> lessons) {
        if (lessons.size() == 0) {
            return;
        }
        var group = groupService.getGroupById(lessons.get(0).getGroupId());
        var users = userService.getUserIdByGroupId(group.id());
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Yekaterinburg"));
        for (UserDto user : users) {
            for (Lesson lesson : lessons) {
                this.sendNotification(user.id(),
                        "Скоро начнется пара",
                        "<b>Пара</b>: " + lesson.getTitle() + "\n" +
                                "<b>Преподаватель:</b> " + lesson.getTeacher() + "\n" +
                                "<b>Начало пары:</b> " + lesson.getTimeStart() + "\n" +
                                "<b>Конец пары:</b> " + lesson.getTimeEnd() + "\n" +
                                "<b>Аудитория</b> : " + lesson.getAuditorium(),
                        LocalDateTime.of(today, lesson.getTimeEnd()));
            }
        }
    }

    @Override
    public void sendNotification(long chatId,
                                 String title,
                                 String text,
                                 LocalDateTime sendTime) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> values = Map.of(
                "type", "SCHEDULE",
                "title", title,
                "chat_id", String.valueOf(chatId),
                "text", text,
                "send_time", sendTime.toString());
        try {
            var message = mapper.writeValueAsString(values);
            rabbitTemplate.convertAndSend("service.notification", "notification-routing-key", message);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
