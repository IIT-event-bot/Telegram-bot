package com.project.scheduleService.service;

import com.project.scheduleService.models.dto.GroupDto;

public interface GroupService {
    GroupDto getGroupById(long id);
    GroupDto getGroupByTitle(String title);
}
