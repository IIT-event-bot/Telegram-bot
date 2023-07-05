package com.project.user_service.repository;

import com.project.user_service.models.Student;
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
            where group_id in :#{#groupsId}
            """, nativeQuery = true)
    List<Student> getStudentsByGroupsId(List<Long> groupsId);
}
