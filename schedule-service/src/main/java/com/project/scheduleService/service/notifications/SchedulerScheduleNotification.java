package com.project.scheduleService.service.notifications;

import com.project.scheduleService.models.DayType;
import com.project.scheduleService.models.Lesson;
import com.project.scheduleService.models.WeekType;
import com.project.scheduleService.repositories.LessonRepository;
import com.project.scheduleService.service.schedule.AcademicYearService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;


@Service
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class SchedulerScheduleNotification {
    private final LessonRepository lessonRepository;
    private final AcademicYearService academicYearService;
    private final ScheduleTelegramNotificationService notificationService;

    @Scheduled(cron = "0 10 0 * * *")
    public void sendSchedule() {
        var dayType = getTodayType();
        var weekType = getWeekTypeToday();
        log.info("Sending schedule on week " + weekType.name() + " day " + dayType.name());
        var lessonsToday = lessonRepository.getAllByWeekTypeAndDayType(weekType, dayType);
        Map<Long, List<Lesson>> groupLessons = lessonsToday.stream().collect(groupingBy(Lesson::getGroupId));
        for (Long groupId : groupLessons.keySet()) {
            var lessons = groupLessons.get(groupId);
            notificationService.sendSchedule(lessons);
        }
    }

    private WeekType getWeekTypeToday() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Yekaterinburg"));
        var semesterStart = academicYearService.getAcademicYear();
        int differenceWeek = (int) semesterStart.getDateStart().until(today, ChronoUnit.WEEKS);
        if (semesterStart.getWeekType().equals(WeekType.SECOND_WEEK)) {
            differenceWeek++;
        }
        return WeekType.get(differenceWeek % 2);
    }

    private DayType getTodayType() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Yekaterinburg"));
        return DayType.get(today.getDayOfWeek().getValue() - 1);
    }
}
