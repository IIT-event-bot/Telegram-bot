package com.project.scheduleService.service;

import com.project.scheduleService.models.AcademicYear;
import com.project.scheduleService.models.DayType;
import com.project.scheduleService.models.WeekType;
import com.project.scheduleService.repositories.AcademicYearRepository;
import com.project.scheduleService.repositories.LessonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;


@Service
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class SchedulerScheduleNotification {
    private final LessonRepository lessonRepository;
    private final AcademicYearRepository academicYearRepository;
    private final ScheduleTelegramNotificationService notificationService;

    @Scheduled(cron = "*/5 * * * * *")
    public void sendSchedule() {
        var dayType = getTodayType();
        log.info(dayType.name());
        var weekType = getWeekTypeToday();
        log.info(weekType.name());
    }

    private WeekType getWeekTypeToday() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Yekaterinburg"));
        var semesterStart = getStartSemesterDate();
        int differenceWeek = (int) semesterStart.getDateStart().until(today, ChronoUnit.WEEKS);
        if (semesterStart.getWeekType().equals(WeekType.SECOND_WEEK)) {
            differenceWeek++;
        }
        return WeekType.values()[differenceWeek % 2];
    }

    private DayType getTodayType() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Yekaterinburg"));
        return DayType.values()[today.getDayOfWeek().getValue() - 1];
    }

    private AcademicYear getStartSemesterDate() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Yekaterinburg"));
        int semesterNumber = today.getMonthValue() < Month.SEPTEMBER.getValue() ? 2 : 1;
        return academicYearRepository.getAcademicYearByDateStartAndSemesterNumber(today.getYear(), semesterNumber);
    }
}
