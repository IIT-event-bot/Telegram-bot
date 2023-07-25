package com.project.scheduleService.service;

import com.project.scheduleService.models.DayType;
import com.project.scheduleService.models.Lesson;
import com.project.scheduleService.models.WeekType;
import com.project.scheduleService.models.dto.DayDto;
import com.project.scheduleService.models.dto.LessonDto;
import com.project.scheduleService.models.dto.ScheduleDto;
import com.project.scheduleService.models.dto.WeekDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleDtoConverter implements ScheduleDtoMapper {
    private final GroupService groupService;

    @Override
    public ScheduleDto convertSchedule(List<Lesson> schedule) {
        if (schedule.size() == 0) {
            throw new IllegalArgumentException("Schedule not exists");
        }
        var group = groupService.getGroupById(schedule.get(0).getGroupId());
        return new ScheduleDto(
                group.title(),
                getWeekByType(schedule, WeekType.FIRST_WEEK),
                getWeekByType(schedule, WeekType.SECOND_WEEK)
        );
    }

    private WeekDto getWeekByType(List<Lesson> schedule, WeekType weekType) {
        var lessons = schedule
                .stream()
                .filter(lesson -> lesson.getWeekType().equals(weekType))
                .toList();

        return new WeekDto(
                weekType.title,
                convertDays(lessons)
        );
    }

    @Override
    public List<DayDto> convertDays(List<Lesson> lessons) {
        Map<DayType, List<Lesson>> lessonsGroup = lessons.stream().collect(groupingBy(Lesson::getDayType));
        List<DayDto> days = new ArrayList<>();
        for (var day : lessonsGroup.keySet()) {
            var dayLessons = lessonsGroup.get(day);
            days.add(new DayDto(
                    day.title,
                    convertLessons(dayLessons)
            ));
        }
        return days;
    }

    @Override
    public List<LessonDto> convertLessons(List<Lesson> lessons) {
        return lessons.stream()
                .map(this::convertLesson)
                .toList();
    }

    @Override
    public LessonDto convertLesson(Lesson lesson) {
        return new LessonDto(
                lesson.getId(),
                lesson.getTitle(),
                lesson.getTeacher(),
                lesson.getAuditorium(),
                lesson.getTimeStart(),
                lesson.getTimeEnd()
        );
    }
}
