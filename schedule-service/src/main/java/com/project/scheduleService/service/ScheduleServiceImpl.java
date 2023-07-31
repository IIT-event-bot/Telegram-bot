package com.project.scheduleService.service;

import com.project.scheduleService.models.AcademicYear;
import com.project.scheduleService.models.Lesson;
import com.project.scheduleService.models.WeekType;
import com.project.scheduleService.models.dto.ScheduleDto;
import com.project.scheduleService.models.dto.WeekDto;
import com.project.scheduleService.repositories.AcademicYearRepository;
import com.project.scheduleService.repositories.LessonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleServiceImpl implements ScheduleService, AcademicYearService {
    private final LessonRepository repository;
    private final ScheduleDtoMapper dtoMapper;
    private final AcademicYearRepository academicYearRepository;

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
        var savedSchedule = repository.getAllByGroupId(lessons.get(0).getGroupId());
        deleteIfNotExist(savedSchedule, lessons);
        repository.saveAll(lessons);
    }

    private void deleteIfNotExist(List<Lesson> lessonList1, List<Lesson> lessonList2) {
        for (Lesson lesson : lessonList1) {
            boolean isExist = lessonList2.stream().anyMatch(l -> l.getId() == lesson.getId());
            if (!isExist) {
                repository.delete(lesson);
            }
        }
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
    public WeekDto getWeek(long groupId, WeekType weekType) {
        var lessons = repository.getAllByGroupIdAndWeekType(groupId, weekType);
        return dtoMapper.convertWeek(lessons, weekType);
    }

    @Transactional
    @Override
    public void setStartAcademicYear(LocalDate date, WeekType weekType) {
        int semesterNumber = getSemesterNumber();
        checkAcademicYearDate(date, semesterNumber);
        AcademicYear savedAcademicYear = academicYearRepository
                .getAcademicYearByDateStartAndSemesterNumber(LocalDate.now().getYear(), semesterNumber);
        if (savedAcademicYear != null) {
            updateAcademicYear(date, weekType);
            return;
        }
        AcademicYear academicYear = new AcademicYear(0L, date, weekType, semesterNumber);
        academicYearRepository.save(academicYear);
    }

    @Override
    public AcademicYear getAcademicYear() {
        int semesterNumber = getSemesterNumber();
        return academicYearRepository
                .getAcademicYearByDateStartAndSemesterNumber(LocalDate.now().getYear(), semesterNumber);
    }

    @Transactional
    @Override
    public void updateAcademicYear(LocalDate date, WeekType weekType) {
        int semesterNumber = getSemesterNumber();
        checkAcademicYearDate(date, semesterNumber);
        AcademicYear savedAcademicYear = academicYearRepository
                .getAcademicYearByDateStartAndSemesterNumber(LocalDate.now().getYear(), semesterNumber);
        savedAcademicYear.setDateStart(date);
        savedAcademicYear.setWeekType(weekType);
        academicYearRepository.save(savedAcademicYear);
    }

    private int getSemesterNumber() {
        return LocalDate.now().getMonthValue() < Month.SEPTEMBER.getValue()
                ? 1
                : 2;
    }

    private void checkAcademicYearDate(LocalDate date, int semesterType) {
        if (semesterType == 1 && date.getMonthValue() < Month.SEPTEMBER.getValue()
                || semesterType == 2 && date.getMonthValue() >= Month.SEPTEMBER.getValue()
                || date.getYear() < LocalDate.now().getYear()) {
            throw new IllegalArgumentException("You can't change last semester");
        }
    }
}
