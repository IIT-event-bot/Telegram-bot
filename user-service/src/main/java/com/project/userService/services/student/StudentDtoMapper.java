package com.project.userService.services.student;

import com.project.userService.models.Student;
import com.project.userService.models.StudentDto;

@FunctionalInterface
public interface StudentDtoMapper {
    StudentDto convert(Student student);
}
