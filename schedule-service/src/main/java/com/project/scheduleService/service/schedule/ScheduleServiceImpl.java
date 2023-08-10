package com.project.scheduleService.service.schedule;

import com.google.protobuf.Timestamp;
import com.project.scheduleService.ScheduleServiceGrpc;
import com.project.scheduleService.ScheduleServiceOuterClass;
import com.project.scheduleService.models.AcademicYear;
import com.project.scheduleService.models.DayType;
import com.project.scheduleService.models.Lesson;
import com.project.scheduleService.models.WeekType;
import com.project.scheduleService.models.dto.ScheduleDto;
import com.project.scheduleService.models.dto.WeekDto;
import com.project.scheduleService.repositories.AcademicYearRepository;
import com.project.scheduleService.repositories.LessonRepository;
import com.project.scheduleService.service.students.StudentService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleServiceImpl extends ScheduleServiceGrpc.ScheduleServiceImplBase
        implements ScheduleService, AcademicYearService {
    private final LessonRepository repository;
    private final ScheduleDtoMapper dtoMapper;
    private final AcademicYearRepository academicYearRepository;
    private final StudentService studentService;

    @Override
    @Transactional
    public ScheduleDto getGroupSchedule(long groupId) {
        var lessons = repository.getAllByGroupId(groupId);
        return dtoMapper.convertSchedule(lessons);
    }

    @Override
    public List<Lesson> getScheduleOnDate(LocalDate date) {
        var day = DayType.get(date.getDayOfWeek().getValue() - 1);
        var semesterStart = getAcademicYear();
        int differenceWeek = (int) semesterStart.getDateStart().until(date, ChronoUnit.WEEKS);
        if (semesterStart.getWeekType().equals(WeekType.SECOND_WEEK)) {
            differenceWeek++;
        }
        var week = WeekType.get(differenceWeek % 2);
        return repository.getAllByWeekTypeAndDayType(week, day);
    }

    @Override
    @Transactional
    public void updateSchedule(ScheduleDto schedule) {
        var lessons = dtoMapper.convertSchedule(schedule);
        var savedSchedule = repository.getAllByGroupId(lessons.get(0).getGroupId());
        deleteIfNotExist(savedSchedule, lessons);
        checkLocalStudentExists(lessons);
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
        if (getAcademicYear() == null) {
            throw new IllegalArgumentException("You can't create schedule without academic year");
        }
        var lessons = dtoMapper.convertSchedule(schedule);
        checkLocalStudentExists(lessons);
        repository.saveAll(lessons);
    }

    @Override
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
        date = validateAcademicYearStartDate(date);
        checkAcademicYearDate(date, semesterNumber);
        AcademicYear savedAcademicYear = academicYearRepository
                .getAcademicYearByDateStartAndSemesterNumber(LocalDate.now().getYear(), semesterNumber);
        if (savedAcademicYear != null) {
            savedAcademicYear.setDateStart(date);
            savedAcademicYear.setWeekType(weekType);
            academicYearRepository.save(savedAcademicYear);
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
        date = validateAcademicYearStartDate(date);
        checkAcademicYearDate(date, semesterNumber);
        AcademicYear savedAcademicYear = academicYearRepository
                .getAcademicYearByDateStartAndSemesterNumber(LocalDate.now().getYear(), semesterNumber);
        savedAcademicYear.setDateStart(date);
        savedAcademicYear.setWeekType(weekType);
        academicYearRepository.save(savedAcademicYear);
    }

    private LocalDate validateAcademicYearStartDate(LocalDate date) {
        if (date.getDayOfWeek().getValue() > DayOfWeek.MONDAY.getValue()) {
            date = date.minusDays(date.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue());
        }
        return date;
    }

    private int getSemesterNumber() {
        return LocalDate.now().getMonthValue() >= Month.SEPTEMBER.getValue()
                ? 1
                : 2;
    }

    private void checkAcademicYearDate(LocalDate date, int semesterType) {
        if ((semesterType == 1 && date.getMonthValue() < Month.SEPTEMBER.getValue())
                || (semesterType == 2 && date.getMonthValue() >= Month.SEPTEMBER.getValue())
                || (date.getYear() < LocalDate.now().getYear())) {
            throw new IllegalArgumentException("You can't change last semester");
        }
    }

    private void checkLocalStudentExists(List<Lesson> lessons) {
        for (Lesson lesson : lessons) {
            for (Long localUser : lesson.getLocalUsers()) {
                studentService.getStudentChatIdById(localUser);
            }
        }
    }

    @Override
    public void getScheduleByGroupId(ScheduleServiceOuterClass.ScheduleRequest request,
                                     StreamObserver<ScheduleServiceOuterClass.ScheduleResponse> responseObserver) {
        var groupId = request.getGroupId();
        var lessons = repository.getAllByGroupId(groupId);

        sendLessonList(responseObserver, groupId, lessons);
    }

    @Override
    public void getScheduleTodayByGroupId(ScheduleServiceOuterClass.ScheduleRequest request,
                                          StreamObserver<ScheduleServiceOuterClass.ScheduleResponse> responseObserver) {
        var groupId = request.getGroupId();
        var lessons = this.getScheduleOnDate(LocalDate.now());

        sendLessonList(responseObserver, groupId, lessons);
    }

    private void sendLessonList(StreamObserver<ScheduleServiceOuterClass.ScheduleResponse> responseObserver,
                                long groupId,
                                List<Lesson> lessons) {
        for (Lesson lesson : lessons) {
            var response = ScheduleServiceOuterClass.ScheduleResponse.newBuilder()
                    .setId(lesson.getId())
                    .setTitle(lesson.getTitle())
                    .setAuditorium(lesson.getAuditorium())
                    .setTimeStart(Timestamp.newBuilder()
                            .setSeconds(lesson.getTimeStart()
                                    .toEpochSecond(LocalDate.now(), ZoneOffset.of("+5"))
                            ).build()
                    )
                    .setTimeEnd(Timestamp.newBuilder()
                            .setSeconds(lesson.getTimeEnd()
                                    .toEpochSecond(LocalDate.now(), ZoneOffset.of("+5"))
                            ).build()
                    )
                    .setWeekType(lesson.getWeekType().name())
                    .setDayType(lesson.getDayType().name())
                    .setGroupId(groupId)
                    .build();
            responseObserver.onNext(response);
        }
        responseObserver.onCompleted();
    }
}
