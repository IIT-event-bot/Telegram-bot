package com.project.scheduleService.service;

import com.project.scheduleService.models.dto.ScheduleDto;
import com.project.scheduleService.models.dto.WeekDto;
import com.project.scheduleService.repositories.LessonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {
    private final LessonRepository repository;
    private final ScheduleDtoMapper dtoMapper;

    @Override
    public ScheduleDto getGroupSchedule(long groupId) {
        var lessons = repository.getAllByGroupId(groupId);
        return dtoMapper.convertSchedule(lessons);
    }

    @Override
    public void updateSchedule(long groupId, ScheduleDto schedule) {

    }

    @Override
    public void createSchedule(ScheduleDto schedule) {

    }

    @Override
    public void deleteSchedule(long id) {
        throw new RuntimeException("Not implemented method");
    }

    @Override
    public WeekDto getWeek(long groupId, String weekTitle) {
        throw new RuntimeException("Not implemented method");
    }
}
