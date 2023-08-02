package com.project.event.services.student;

import com.project.event.models.dto.GroupDto;
import com.project.event.models.dto.StudentDto;

import java.util.List;

public interface StudentService {
    List<Long> getStudentChatIdByGroupId(Long groupId);

    Long getChatIdByStudentId(Long studentId);

    StudentDto getStudentById(Long studentId);

    GroupDto getGroupById(Long groupId);

    StudentDto getStudentByUserId(Long userId);
}
