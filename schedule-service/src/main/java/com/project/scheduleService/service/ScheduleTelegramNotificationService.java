package com.project.scheduleService.service;

import com.project.scheduleService.models.Lesson;

import java.util.List;

public interface ScheduleTelegramNotificationService extends TelegramNotificationService{
    void sendSchedule(List<Lesson> lessons);
}
