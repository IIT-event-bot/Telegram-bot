package com.project.scheduleService.repositories;

import com.project.scheduleService.models.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> getAllByGroupId(long groupId);
}
