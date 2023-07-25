package com.project.scheduleService.service;

import com.project.scheduleService.models.dto.ScheduleDto;
import com.project.scheduleService.models.dto.WeekDto;

public interface ScheduleService {
    ScheduleDto getGroupSchedule(long groupId);

    void updateSchedule(ScheduleDto schedule);

    void createSchedule(ScheduleDto schedule);

    void deleteSchedule(long groupId);

    WeekDto getWeek(long groupId, String weekTitle);
}
