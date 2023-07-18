package com.project.userService.services.group;

import com.project.groupService.GroupServiceOuterClass;
import com.project.userService.models.Group;
import com.project.userService.repository.GroupRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends com.project.groupService.GroupServiceGrpc.GroupServiceImplBase
        implements GroupService {
    private final GroupRepository repository;

    @Override
    public Group getGroupById(long groupId) {
        return repository.getGroupById(groupId);
    }

    @Override
    public List<Group> getGroupsLikeTitle(String title) {
        if (title == null) {
            return getAllGroups();
        }
        return repository.getGroupLikeTitle(title);
    }

    @Override
    public List<Group> getAllGroups() {
        return repository.findAll();
    }

    @Override
    public Group getGroupByTitle(String title) {
        return repository.getGroupByTitle(title);
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
    public void getGroupByGroupId(GroupServiceOuterClass.GroupRequest request,
                                  StreamObserver<GroupServiceOuterClass.GroupResponse> responseObserver) {
        var groupId = request.getGroupId();
        var group = getGroupById(groupId);

        var response = GroupServiceOuterClass.GroupResponse.newBuilder()
                .setId(group.getId())
                .setTitle(group.getTitle())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateGroup(Group group) {
        repository.save(group);
    }

    @Override
    public List<Group> getGroupsByIds(List<Long> ids) {
        return repository.getGroupsById(ids);
    }
}
