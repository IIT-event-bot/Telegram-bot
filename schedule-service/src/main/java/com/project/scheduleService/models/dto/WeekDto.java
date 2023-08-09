package com.project.scheduleService.models.dto;

import java.util.List;

public record WeekDto(
        String title,
        List<DayDto> days
) {
}
