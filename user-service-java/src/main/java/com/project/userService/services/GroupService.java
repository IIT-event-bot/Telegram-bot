package com.project.userService.services;

import com.project.userService.models.Group;

import java.util.List;

public interface GroupService {
    Group getGroupById(long groupId);

    List<Group> getGroupsByTitle(String title);

    List<Group> getAllGroups();

    void createGroup(Group group);

    void deleteGroupById(long groupId);

    void updateGroup(Group group);
}
