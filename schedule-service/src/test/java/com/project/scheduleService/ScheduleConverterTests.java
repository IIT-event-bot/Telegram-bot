package com.project.scheduleService;

import com.project.scheduleService.models.DayType;
import com.project.scheduleService.models.Lesson;
import com.project.scheduleService.models.WeekType;
import com.project.scheduleService.models.dto.*;
import com.project.scheduleService.service.GroupServiceImpl;
import com.project.scheduleService.service.ScheduleDtoConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalTime;
import java.util.List;

public class ScheduleConverterTests {

    @InjectMocks
    private ScheduleDtoConverter dtoConverter;

    @Mock
    private GroupServiceImpl groupService;

    private final LocalTime time = LocalTime.now();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private ScheduleDto getDto() {
        return new ScheduleDto(
                "ПрИ-302",
                new WeekDto(
                        WeekType.FIRST_WEEK.title,
                        List.of(
                                new DayDto(
                                        DayType.MONDAY.title,
                                        List.of(
                                                new LessonDto(
                                                        1,
                                                        "title",
                                                        "teacher",
                                                        "auditorium",
                                                        time,
                                                        time.plusHours(1)
                                                )
                                        )
                                )
                        )
                ),
                new WeekDto(
                        WeekType.SECOND_WEEK.title,
                        List.of(
                                new DayDto(
                                        DayType.TUESDAY.title,
                                        List.of(
                                                new LessonDto(
                                                        2,
                                                        "title",
                                                        "teacher",
                                                        "auditorium",
                                                        time,
                                                        time.plusHours(1)
                                                )
                                        )
                                )
                        )
                )
        );
    }

    private List<Lesson> getSchedule() {
        return List.of(
                new Lesson(
                        1,
                        "title",
                        "teacher",
                        "auditorium",
                        time,
                        time.plusHours(1),
                        WeekType.FIRST_WEEK,
                        DayType.MONDAY,
                        List.of(1L),
                        2L
                ),
                new Lesson(
                        2,
                        "title",
                        "teacher",
                        "auditorium",
                        time,
                        time.plusHours(1),
                        WeekType.SECOND_WEEK,
                        DayType.TUESDAY,
                        List.of(1L),
                        2L
                )
        );
    }

    @Test
    public void testScheduleConverter() {
        ScheduleDto dto = getDto();
        List<Lesson> schedule = getSchedule();

        Mockito.when(this.groupService.getGroupById(2)).thenReturn(new GroupDto(2, "ПрИ-302"));

        var convert = this.dtoConverter.convertSchedule(schedule);

        Assertions.assertEquals(dto, convert);
    }

    @Test
    public void testWrongScheduleConvert() {
        ScheduleDto dto = getDto();
        List<Lesson> schedule = getSchedule();
        schedule.get(0).setDayType(DayType.FRIDAY);

        Mockito.when(this.groupService.getGroupById(2)).thenReturn(new GroupDto(2, "ПрИ-302"));

        var convert = this.dtoConverter.convertSchedule(schedule);

        Assertions.assertNotEquals(dto, convert);
    }
}
