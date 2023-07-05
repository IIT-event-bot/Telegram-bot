package com.project.userService.repository;

import com.project.userService.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Student getStudentById(long id);

    Student getStudentByGroupId(long groupId);

    void deleteStudentById(long id);

    @Modifying
    @Transactional
    @Query(value = """
            select *
            from students
            where group_id in :#{#groupsIds}
            """, nativeQuery = true)
    List<Student> getStudentsByGroupId(List<Long> groupsIds);

    @Modifying
    @Transactional
    @Query(value = """
            select *
            from students
            where group_id = :#{#groupId}
            """, nativeQuery = true)
    List<Student> getStudentsByGroupId(long groupId);
}
