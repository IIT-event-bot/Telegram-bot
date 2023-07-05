package com.project.event.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "events")
@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title")
    private String title;

    @Column(name = "text")
    private String text;

    @Column(name = "has_feedback")
    private boolean hasFeedback;

    @Column(name = "event_time")
    private LocalDateTime eventTime;

    @Column(name = "is_group_event")
    private boolean isGroupEvent;

    @Column(name = "is_student_event")
    private boolean isStudentEvent;

    @Column(name = "type")
    @Enumerated(value = EnumType.STRING)
    private EventType type;
}
