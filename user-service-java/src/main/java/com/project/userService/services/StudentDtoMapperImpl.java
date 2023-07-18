package com.project.userService.services;

import com.project.userService.models.Student;
import com.project.userService.models.StudentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudentDtoMapperImpl implements StudentDtoMapper {
    private final GroupService groupService;
    private final UserService userService;

    @Override
    public StudentDto mapStudent(Student student) {
        var group = groupService.getGroupById(student.getGroupId());
        var user = userService.getUserById(student.getUserId());
        return new StudentDto(student.getId(),
                student.getName(),
                student.getSurname(),
                student.getPatronymic(),
                group.getTitle(),
                user.getUsername());
    }
}
