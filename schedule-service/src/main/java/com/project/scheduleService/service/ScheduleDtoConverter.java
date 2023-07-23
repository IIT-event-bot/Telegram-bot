package com.project.scheduleService.service;

import com.project.scheduleService.models.DayType;
import com.project.scheduleService.models.Lesson;
import com.project.scheduleService.models.Schedule;
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
    public ScheduleDto convert(Schedule schedule) {
        var group = groupService.getGroupById(schedule.getGroupId());
        return new ScheduleDto(
                group.title(),
                getWeekByType(schedule, WeekType.FIRST_WEEK),
                getWeekByType(schedule, WeekType.SECOND_WEEK)
        );
    }

    private WeekDto getWeekByType(Schedule schedule, WeekType weekType) {
        var lessons = schedule.getLessons().stream().filter(lesson -> lesson.getWeekType() == weekType).toList();

        return new WeekDto(
                weekType.title,
                convertLessons(lessons)
        );
    }

    private List<DayDto> convertLessons(List<Lesson> lessons) {
        Map<DayType, List<Lesson>> lessonsGroup = lessons.stream().collect(groupingBy(Lesson::getDayType));
        List<DayDto> days = new ArrayList<>();
        for (var day : lessonsGroup.keySet()) {
            var dayLessons = lessonsGroup.get(day);
            days.add(new DayDto(
                    day.title,
                    convertLessonsToDto(dayLessons)
            ));
        }
        return days;
    }

    private List<LessonDto> convertLessonsToDto(List<Lesson> lessons) {
        return lessons.stream()
                .map(lesson -> new LessonDto(
                        lesson.getId(),
                        lesson.getTitle(),
                        lesson.getTeacher(),
                        lesson.getAuditorium(),
                        lesson.getTimeStart(),
                        lesson.getTimeEnd()
                ))
                .toList();
    }
}
