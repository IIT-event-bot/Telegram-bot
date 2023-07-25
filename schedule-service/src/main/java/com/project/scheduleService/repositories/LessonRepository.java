package com.project.scheduleService.repositories;

import com.project.scheduleService.models.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
}
