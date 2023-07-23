package com.project.scheduleService.service;

import com.project.scheduleService.models.Schedule;
import com.project.scheduleService.models.dto.ScheduleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleDtoConverter implements ScheduleDtoMapper {
    @Override
    public ScheduleDto convert(Schedule schedule) {
        return null;
    }
}
