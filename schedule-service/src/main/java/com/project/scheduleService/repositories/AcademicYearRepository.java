package com.project.scheduleService.repositories;

import com.project.scheduleService.models.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> {
    @Transactional
    @Query(value = """
            select *
            from academic_year
            where to_number(to_char(date_start, 'yyyy'), 'FMS9999') = :#{#year}
              and semester_number = :#{#semesterNumber}
            """, nativeQuery = true)
    AcademicYear getAcademicYearByDateStartAndSemesterNumber(int year, int semesterNumber);
}
