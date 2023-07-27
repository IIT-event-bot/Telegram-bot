package com.project.scheduleService.service;

import com.project.groupService.GroupServiceGrpc;
import com.project.groupService.GroupServiceOuterClass;
import com.project.scheduleService.models.dto.GroupDto;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static io.grpc.Status.UNAVAILABLE;
import static io.grpc.Status.UNKNOWN;

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
        } catch (StatusRuntimeException e) {
            if (e.getStatus().equals(UNKNOWN)) {
                throw new IllegalArgumentException("Group with id '" + id + "' does not exists");
            }
            if (e.getStatus().getCode().equals(UNAVAILABLE.getCode())) {
                throw new RuntimeException("Group service not available");
            }
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException();
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
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode().equals(UNKNOWN.getCode())) {
                throw new IllegalArgumentException("Group with title '" + title + "' does not exists");
            }
            if (e.getStatus().getCode().equals(UNAVAILABLE.getCode())) {
                throw new RuntimeException("Group service not available");
            }
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException();
        }
    }
}
