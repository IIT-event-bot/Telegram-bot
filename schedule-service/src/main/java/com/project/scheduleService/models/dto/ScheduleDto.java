package com.project.scheduleService.models.dto;

public record ScheduleDto(
        String groupName,
        WeekDto firstWeek,
        WeekDto secondWeek
) {
}
