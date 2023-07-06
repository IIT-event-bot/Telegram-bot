package com.project.event.repositories;

import com.project.event.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    Event getEventById(long id);

    List<Event> getEventsByEventTimeBetween(LocalDateTime from, LocalDateTime to);

    @Modifying
    @Transactional
    @Query(value = """
            select distinct events.*
            from events
            join event_repeat er
                on events.id = er.event_id
            where er.repeat_time between :#{#from} and :#{#to}
            """, nativeQuery = true)
    List<Event> getEventsByRepeatTimeBetween(LocalDateTime from, LocalDateTime to);
}
