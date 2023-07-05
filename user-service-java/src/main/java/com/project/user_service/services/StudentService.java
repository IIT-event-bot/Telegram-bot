package com.project.user_service.services;

import com.project.user_service.models.Student;

import java.util.List;

public interface StudentService {
    Student getStudentById(long id);

    void saveStudent(Student student);

    List<Student> getAllStudents();

    List<Student> getStudentsByGroup(String groupName);

    void deleteStudentById(long studentId);

    void updateStudent(Student student);
}
