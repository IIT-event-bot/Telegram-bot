package com.project.scheduleService.service;

import com.project.scheduleService.models.dto.ScheduleDto;
import com.project.scheduleService.models.dto.WeekDto;
import com.project.scheduleService.repositories.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository repository;
    private final ScheduleDtoMapper dtoMapper;

    @Override
    public ScheduleDto getGroupSchedule(long groupId) {
        throw new IllegalArgumentException("Not implemented method");
    }

    @Override
    public void updateSchedule(long groupId, ScheduleDto schedule) {
        throw new IllegalArgumentException("Not implemented method");
    }

    @Override
    public void createSchedule(long groupId, ScheduleDto schedule) {
        throw new IllegalArgumentException("Not implemented method");
    }

    @Override
    public void deleteSchedule(long id) {
        throw new IllegalArgumentException("Not implemented method");
    }

    @Override
    public WeekDto getWeek(long groupId, String weekTitle) {
        throw new IllegalArgumentException("Not implemented method");
    }
}
