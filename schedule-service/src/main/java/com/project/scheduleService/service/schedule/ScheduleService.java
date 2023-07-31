package com.project.scheduleService.service.schedule;

import com.project.scheduleService.models.Lesson;
import com.project.scheduleService.models.WeekType;
import com.project.scheduleService.models.dto.ScheduleDto;
import com.project.scheduleService.models.dto.WeekDto;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {
    ScheduleDto getGroupSchedule(long groupId);

    List<Lesson> getScheduleOnDate(LocalDate date);

    void updateSchedule(ScheduleDto schedule);

    void createSchedule(ScheduleDto schedule);

    void deleteSchedule(long groupId);

    WeekDto getWeek(long groupId, WeekType weekType);
}
