package com.project.scheduleService.service;

import com.project.scheduleService.models.WeekType;
import com.project.scheduleService.models.dto.ScheduleDto;
import com.project.scheduleService.models.dto.WeekDto;

import java.time.LocalDateTime;

public interface ScheduleService {
    ScheduleDto getGroupSchedule(long groupId);

    ScheduleDto getScheduleOnDate(LocalDateTime date);

    void updateSchedule(ScheduleDto schedule);

    void createSchedule(ScheduleDto schedule);

    void deleteSchedule(long groupId);

    WeekDto getWeek(long groupId, WeekType weekType);
}
