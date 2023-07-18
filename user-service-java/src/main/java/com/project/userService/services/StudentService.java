package com.project.userService.services;

import com.project.userService.models.Student;
import com.project.userService.models.StudentDto;

import java.util.List;

public interface StudentService {
    StudentDto getStudentById(long id);

    void saveStudent(Student student);

    List<StudentDto> getAllStudents();

    List<StudentDto> getStudentsByGroup(String groupName);

    void deleteStudentById(long studentId);

    void updateStudent(Student student);
}
