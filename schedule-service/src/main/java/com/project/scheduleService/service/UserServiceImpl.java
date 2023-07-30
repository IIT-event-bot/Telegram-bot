package com.project.scheduleService.service;

import com.project.scheduleService.models.dto.UserDto;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.project.studentService.StudentServiceGrpc;
import com.project.studentService.StudentServiceOuterClass;

import java.util.ArrayList;
import java.util.List;

import static io.grpc.Status.UNAVAILABLE;
import static io.grpc.Status.UNKNOWN;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    @Value("${grpc.userservice.host}")
    private String host;

    @Value("${grpc.userservice.port}")
    private int port;

    @Override
    public List<UserDto> getUserIdByGroupId(long groupId) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();

        var stub = com.project.studentService.StudentServiceGrpc.newBlockingStub(channel);

        var request = com.project.studentService.StudentServiceOuterClass.GroupRequest
                .newBuilder()
                .setGroupId(groupId)
                .build();
        List<UserDto> users = new ArrayList<>();
        try {
            var response = stub.getStudentsChatIdByGroupId(request);
            while (response.hasNext()) {
                var user = response.next();
                users.add(new UserDto(user.getId(), user.getUsername()));
            }
            return users;
        } catch (StatusRuntimeException e) {
            if (e.getStatus().equals(UNKNOWN)) {
                final String errorMessage = "Group with id '" + groupId + "' does not exists";
                log.error(errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }
            if (e.getStatus().getCode().equals(UNAVAILABLE.getCode())) {
                final String errorMessage = "Group service not available";
                log.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException();
        }
    }
}
