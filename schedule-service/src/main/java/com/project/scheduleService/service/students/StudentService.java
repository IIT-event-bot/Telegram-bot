package com.project.scheduleService.service.students;

import com.project.scheduleService.models.dto.UserDto;

import java.util.List;

public interface StudentService {
    List<UserDto> getUserIdByGroupId(long groupId);

    UserDto getStudentChatIdById(long studentId);
}
