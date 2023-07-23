package com.project.scheduleService.repositories;

import com.project.scheduleService.models.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Lesson, Long> {
}
