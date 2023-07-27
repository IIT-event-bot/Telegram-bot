package com.project.scheduleService.service;

import com.project.scheduleService.models.dto.GroupDto;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.project.groupService.GroupServiceGrpc;
import com.project.groupService.GroupServiceOuterClass;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupServiceImpl implements GroupService {
    @Value("${grpc.userservice.host}")
    private String userServiceHost;
    @Value("${grpc.userservice.port}")
    private int userServicePort;

    @Override
    public GroupDto getGroupById(long id) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(userServiceHost, userServicePort)
                .usePlaintext()
                .build();

        var stub = com.project.groupService.GroupServiceGrpc.newBlockingStub(channel);

        var request = com.project.groupService.GroupServiceOuterClass.GroupRequest
                .newBuilder()
                .setGroupId(id)
                .build();

        try {
            var response = stub.getGroupByGroupId(request);
            return new GroupDto(response.getId(), response.getTitle());
        }catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException("Group with id does not exist");
        }
    }

    @Override
    public GroupDto getGroupByTitle(String title) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(userServiceHost, userServicePort)
                .usePlaintext()
                .build();

        var stub = com.project.groupService.GroupServiceGrpc.newBlockingStub(channel);

        var request = com.project.groupService.GroupServiceOuterClass.GroupTitleRequest
                .newBuilder()
                .setTitle(title)
                .build();

        try {
            var response = stub.getGroupByTitle(request);
            return new GroupDto(response.getId(), response.getTitle());
        }catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException("Group with id does not exist");
        }
    }
}
