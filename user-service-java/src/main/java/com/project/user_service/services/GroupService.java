package com.project.user_service.services;

import com.project.user_service.models.Group;

import java.util.List;

public interface GroupService {
    Group getGroupById(long groupId);

    List<Group> getGroupsByTitle(String title);

    List<Group> getAllGroups();

    void createGroup(Group group);

    void deleteGroupById(long groupId);

    void updateGroup(Group group);
}
