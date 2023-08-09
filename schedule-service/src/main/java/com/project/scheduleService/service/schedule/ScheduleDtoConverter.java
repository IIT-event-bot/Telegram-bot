package com.project.scheduleService.service.schedule;

import com.project.scheduleService.models.DayType;
import com.project.scheduleService.models.Lesson;
import com.project.scheduleService.models.WeekType;
import com.project.scheduleService.models.dto.DayDto;
import com.project.scheduleService.models.dto.LessonDto;
import com.project.scheduleService.models.dto.ScheduleDto;
import com.project.scheduleService.models.dto.WeekDto;
import com.project.scheduleService.service.students.GroupService;
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
                convertWeek(schedule, WeekType.FIRST_WEEK),
                convertWeek(schedule, WeekType.SECOND_WEEK)
        );
    }

    @Override
    public WeekDto convertWeek(List<Lesson> schedule, WeekType weekType) {
        var lessons = schedule
                .stream()
                .filter(lesson -> lesson.getWeekType().equals(weekType))
                .toList();

        return new WeekDto(
                weekType.name(),
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
                    day.name(),
                    convertLessons(dayLessons)
            ));
        }
        return days;
    }

    @Override
    public List<Lesson> convertSchedule(ScheduleDto schedule) {
        var group = groupService.getGroupByTitle(schedule.groupName());
        var lessons = convertWeekToLesson(schedule.firstWeek(), group.id());
        lessons.addAll(convertWeekToLesson(schedule.secondWeek(), group.id()));
        return lessons;
    }

    private List<Lesson> convertWeekToLesson(WeekDto week, long groupId) {
        return convertDaysToLesson(week.days(), WeekType.valueOf(week.title()), groupId);
    }

    private List<Lesson> convertDaysToLesson(List<DayDto> days, WeekType weekType, long groupId) {
        List<Lesson> lessons = new ArrayList<>();
        for (DayDto day : days) {
            lessons.addAll(convertLessonDtoToLesson(day.lessons(), weekType, DayType.valueOf(day.title()), groupId));
        }
        return lessons;
    }

    private List<Lesson> convertLessonDtoToLesson(List<LessonDto> lessons, WeekType weekType, DayType dayType, long groupId) {
        return lessons.stream()
                .map(lesson -> convertLesson(lesson, weekType, dayType, groupId))
                .toList();
    }

    private Lesson convertLesson(LessonDto lesson, WeekType weekType, DayType dayType, long groupId) {
        return Lesson.builder()
                .id(lesson.id())
                .title(lesson.title())
                .teacher(lesson.teacher())
                .auditorium(lesson.auditorium())
                .timeStart(lesson.timeStart())
                .timeEnd(lesson.timeEnd())
                .weekType(weekType)
                .dayType(dayType)
                .groupId(groupId)
                .localUsers(lesson.localUsers())
                .build();
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
                lesson.getTimeEnd(),
                lesson.getLocalUsers()
        );
    }
}
