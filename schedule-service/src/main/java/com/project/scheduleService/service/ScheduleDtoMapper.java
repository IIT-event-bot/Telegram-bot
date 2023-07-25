package com.project.scheduleService.service;

import com.project.scheduleService.models.Lesson;
import com.project.scheduleService.models.dto.DayDto;
import com.project.scheduleService.models.dto.LessonDto;
import com.project.scheduleService.models.dto.ScheduleDto;

import java.util.List;

public interface ScheduleDtoMapper {
    ScheduleDto convertSchedule(List<Lesson> schedule);

    List<LessonDto> convertLessons(List<Lesson> lessons);

    LessonDto convertLesson(Lesson lesson);

    List<DayDto> convertDays(List<Lesson> lessons);

    List<Lesson> convertSchedule(ScheduleDto schedule);
}
