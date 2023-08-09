package com.project.scheduleService.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "academic_year")
public class AcademicYear {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "date_start")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate dateStart;

    @Column(name = "week_type")
    @Enumerated(EnumType.STRING)
    private WeekType weekType;

    @Column(name = "semester_number")
    private int semesterNumber;
}
