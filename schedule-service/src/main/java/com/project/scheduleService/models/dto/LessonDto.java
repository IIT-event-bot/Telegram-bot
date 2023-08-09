package com.project.scheduleService.models.dto;

import java.time.LocalTime;
import java.util.List;

public record LessonDto(
        long id,
        String title,
        String teacher,
        String auditorium,
        LocalTime timeStart,
        LocalTime timeEnd,
        List<Long> localUsers
) {
}
