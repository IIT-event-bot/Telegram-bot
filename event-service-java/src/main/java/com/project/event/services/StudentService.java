package com.project.event.services;

import com.project.event.models.GroupDto;
import com.project.event.models.StudentDto;

import java.util.List;

public interface StudentService {
    List<Long> getStudentChatIdByGroupId(Long groupId);

    Long getStudentChatIdById(Long studentId);

    StudentDto getStudentById(Long studentId);

    GroupDto getGroupById(Long groupId);

    StudentDto getStudentByUserId(Long userId);
}
