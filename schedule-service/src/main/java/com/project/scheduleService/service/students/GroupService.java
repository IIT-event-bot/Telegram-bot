package com.project.scheduleService.service.students;

import com.project.scheduleService.models.dto.GroupDto;

public interface GroupService {
    GroupDto getGroupById(long id);

    GroupDto getGroupByTitle(String title);
}
