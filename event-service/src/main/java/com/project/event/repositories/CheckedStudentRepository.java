package com.project.event.repositories;

import com.project.event.models.EventCheck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CheckedStudentRepository extends JpaRepository<EventCheck, Long> {
    List<EventCheck> getEventChecksByEventId(long eventId);
}
