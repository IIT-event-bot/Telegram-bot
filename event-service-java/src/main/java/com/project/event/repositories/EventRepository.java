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

    @Modifying
    @Transactional
    @Query(value = """
            select *
            from events
            where has_feedback
              and event_time between :#{#from} and :#{#to}
            """, nativeQuery = true)
    List<Event> getEventByFeedbackTime(LocalDateTime from, LocalDateTime to);

    @Modifying
    @Transactional
    @Query(value = """
            select *
            from events
            join practice.public.event_group eg
                on events.id = eg.event_id
            where title like '%'||:#{#title}||'%'
              and to_date(event_time, 'dd-MM-yyyy') = :#{#date}
              and eg.group_id = :#{#groupId}
            """, nativeQuery = true)
    List<Event> getEventsByFilter(String title, LocalDateTime date, Long groupId);

    List<Event> getEventsByTitleLikeIgnoreCase(String title);

    @Modifying
    @Transactional
    @Query(value = """
            select *
            from events
            where to_date(event_time, 'dd-MM-yyyy') = :#{#date}
            """, nativeQuery = true)
    List<Event> getEventsByEventDate(LocalDateTime date);

    @Modifying
    @Transactional
    @Query(value = """
            select *
            from events
            join public.event_group eg
                on events.id = eg.event_id
            where eg.group_id = :#{#groupId}
            """, nativeQuery = true)
    List<Event> getEventsByGroupId(Long groupId);
}
