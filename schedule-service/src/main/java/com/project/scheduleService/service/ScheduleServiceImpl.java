package com.project.scheduleService.service;

import com.project.scheduleService.models.WeekType;
import com.project.scheduleService.models.dto.ScheduleDto;
import com.project.scheduleService.models.dto.WeekDto;
import com.project.scheduleService.repositories.LessonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {
    private final LessonRepository repository;
    private final ScheduleDtoMapper dtoMapper;

    @Override
    @Transactional
    public ScheduleDto getGroupSchedule(long groupId) {
        var lessons = repository.getAllByGroupId(groupId);
        return dtoMapper.convertSchedule(lessons);
    }

    @Override
    public ScheduleDto getScheduleOnDate(LocalDateTime date) {
        throw new RuntimeException("Not implemented method");
    }

    @Override
    @Transactional
    public void updateSchedule(ScheduleDto schedule) {
        var lessons = dtoMapper.convertSchedule(schedule);
        repository.saveAll(lessons);
    }

    @Override
    @Transactional
    public void createSchedule(ScheduleDto schedule) {
        var lessons = dtoMapper.convertSchedule(schedule);
        repository.saveAll(lessons);
    }

    @Override
    @Transactional
    public void deleteSchedule(long id) {
        repository.deleteByGroupId(id);
    }

    @Override
    @Transactional
    public WeekDto getWeek(long groupId, String weekTitle) {
        WeekType weekType = WeekType.valueOf(weekTitle.toUpperCase());
        var lessons = repository.getAllByGroupIdAndWeekType(groupId, weekType);
        return dtoMapper.convertWeek(lessons, weekType);
    }

    @Override
    public void setStartAcademicYear(LocalDate date) {
        throw new RuntimeException("Not implemented method");
    }
}
