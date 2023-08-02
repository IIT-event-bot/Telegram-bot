package com.project.event.repositories;

import com.project.event.models.EventGrade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventGradeRepository extends JpaRepository<EventGrade, Long> {
    List<EventGrade> getEventGradesByEventId(long eventId);
}
