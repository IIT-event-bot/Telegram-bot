package com.project.userService.services.student;

import com.project.userService.models.Student;
import com.project.userService.models.StudentDto;
import com.project.userService.services.group.GroupService;
import com.project.userService.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudentDtoMapperImpl implements StudentDtoMapper {
    private final GroupService groupService;
    private final UserService userService;

    @Override
    public StudentDto convert(Student student) {
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
