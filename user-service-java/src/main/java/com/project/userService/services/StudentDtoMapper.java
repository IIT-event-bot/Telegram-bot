package com.project.userService.services;

import com.project.userService.models.Student;
import com.project.userService.models.StudentDto;

public interface StudentDtoMapper {
    StudentDto mapStudent(Student student);
}
