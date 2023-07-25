package com.project.scheduleService.service;

import com.project.scheduleService.models.dto.GroupDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupServiceImpl implements GroupService {
    @Override
    public GroupDto getGroupById(long id) {
        return new GroupDto(1, "ПрИ-301");//TODO
    }

    @Override
    public GroupDto getGroupByTitle(String title) {
        return new GroupDto(1, title);//TODO
    }
}
