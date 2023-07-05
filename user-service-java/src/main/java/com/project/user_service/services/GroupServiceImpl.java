package com.project.user_service.services;

import com.project.user_service.models.Group;
import com.project.user_service.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final GroupRepository repository;

    @Override
    public Group getGroupById(long groupId) {
        return repository.getGroupById(groupId);
    }

    @Override
    public List<Group> getGroupsByTitle(String title) {
        if (title == null) {
            return getAllGroups();
        }
        return repository.getGroupByTitle(title);
    }

    @Override
    public List<Group> getAllGroups() {
        return repository.findAll();
    }

    @Override
    public void createGroup(Group group) {
        repository.save(group);
    }

    @Override
    public void deleteGroupById(long groupId) {
        repository.deleteGroupById(groupId);
    }

    @Override
    public void updateGroup(Group group) {
        repository.save(group);
    }
}
