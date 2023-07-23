package com.project.scheduleService.service;

import com.project.scheduleService.models.Schedule;
import com.project.scheduleService.models.dto.ScheduleDto;

public interface ScheduleDtoMapper {
    ScheduleDto convert(Schedule lessons);
}
