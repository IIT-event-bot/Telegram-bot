package com.project.event.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

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

    @Column(name = "text", columnDefinition = "TEXT")
    private String text;

    @Column(name = "has_feedback")
    private boolean hasFeedback;

    @Column(name = "event_time")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime eventTime;

    @Column(name = "is_group_event")
    @JsonProperty("isGroupEvent")
    private boolean isGroupEvent;

    @Column(name = "is_student_event")
    @JsonProperty("isStudentEvent")
    private boolean isStudentEvent;

    @Column(name = "is_repeat")
    @JsonProperty("isRepeat")
    private boolean isRepeat;

    @Column(name = "type")
    @Enumerated(value = EnumType.STRING)
    private NotificationType type;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "event_student", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "student_id", nullable = false)
    private List<Long> students;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "event_group", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "group_id", nullable = false)
    private List<Long> groups;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "event_repeat", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "repeat_time", nullable = false)
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private List<LocalDateTime> repeatTime;
}
