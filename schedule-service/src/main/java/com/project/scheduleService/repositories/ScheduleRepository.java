package com.project.scheduleService.repositories;

import com.project.scheduleService.models.Lesson;
import com.project.scheduleService.models.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Modifying
    @Transactional
    @Query(value = """
            select lessons.*
            from schedules
            join lessons
                on schedules.id = lessons.schedule_id
            where schedules.group_id = :#{#groupId}
              and lessons.week = :#{#week}
            """, nativeQuery = true)
    List<Lesson> getAllByGroupIdAndWeekType(long groupId, String week);
}
