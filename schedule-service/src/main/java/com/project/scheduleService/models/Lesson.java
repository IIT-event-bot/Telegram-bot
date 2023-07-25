package com.project.scheduleService.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "lessons")
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title")
    private String title;

    @Column(name = "teacher")
    private String teacher;

    @Column(name = "auditorium")
    private String auditorium;

    @Column(name = "time_start")
    private LocalTime timeStart;

    @Column(name = "time_end")
    private LocalTime timeEnd;

    @Column(name = "week")
    @Enumerated(EnumType.STRING)
    private WeekType weekType;

    @Column(name = "day")
    @Enumerated(EnumType.STRING)
    private DayType dayType;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "lesson_user", joinColumns = @JoinColumn(name = "lesson_id"))
    @Column(name = "user_id", nullable = false)
    @JsonIgnore
    private List<Long> localUsers;

    @Column(name = "group_id")
    private long groupId;
}
