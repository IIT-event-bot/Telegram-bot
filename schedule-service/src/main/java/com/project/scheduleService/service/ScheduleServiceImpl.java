package com.project.scheduleService.service;

import com.project.scheduleService.models.dto.ScheduleDto;
import com.project.scheduleService.models.dto.WeekDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleServiceImpl implements ScheduleService{
    @Override
    public ScheduleDto getGroupSchedule(long groupId) {
        return null;
    }

    @Override
    public void updateSchedule(long groupId, ScheduleDto schedule) {

    }

    @Override
    public void createSchedule(long groupId, ScheduleDto schedule) {

    }

    @Override
    public void deleteSchedule(long id) {

    }

    @Override
    public WeekDto getWeek(long groupId, String weekTitle) {
        return null;
    }
}
