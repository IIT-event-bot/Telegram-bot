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
        throw new IllegalArgumentException("Not implemented method");
    }
}
