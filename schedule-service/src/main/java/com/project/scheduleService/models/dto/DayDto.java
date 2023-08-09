package com.project.scheduleService.models.dto;

import java.util.List;

public record DayDto(
        String title,
        List<LessonDto> lessons
) {
}
