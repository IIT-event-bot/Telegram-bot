package com.project.scheduleService.service;

import com.project.scheduleService.models.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUserIdByGroupId(long groupId);
}
