package com.project.userService.services;

import com.project.userService.models.Student;

import java.util.List;

public interface StudentService {
    Student getStudentById(long id);

    void saveStudent(Student student);

    List<Student> getAllStudents();

    List<Student> getStudentsByGroup(String groupName);

    void deleteStudentById(long studentId);

    void updateStudent(Student student);
}
