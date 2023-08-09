package com.project.scheduleService.service.schedule;

import com.project.scheduleService.models.AcademicYear;
import com.project.scheduleService.models.WeekType;

import java.time.LocalDate;

public interface AcademicYearService {
    void setStartAcademicYear(LocalDate date, WeekType weekType);

    AcademicYear getAcademicYear();

    void updateAcademicYear(LocalDate date, WeekType weekType);
}
