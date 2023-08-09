package com.project.scheduleService.repositories;

import com.project.scheduleService.models.DayType;
import com.project.scheduleService.models.Lesson;
import com.project.scheduleService.models.WeekType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> getAllByGroupId(long groupId);

    @Modifying
    @Transactional
    void deleteByGroupId(long groupId);

    List<Lesson> getAllByGroupIdAndWeekType(long groupId, WeekType weekType);

    List<Lesson> getAllByWeekTypeAndDayType(WeekType weekType, DayType dayType);

    List<Lesson> getAllByAuditoriumAndWeekTypeAndDayType(String auditorium, WeekType weekType, DayType dayType);
}
