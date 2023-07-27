package com.project.event.services;

import com.project.event.models.GroupDto;
import com.project.event.models.StudentDto;
import com.project.studentService.StudentServiceGrpc;
import com.project.studentService.StudentServiceOuterClass;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class StudentServiceImpl implements StudentService {
    @Value("${grpc.userservice.host}")
    private String userServiceHost;
    @Value("${grpc.userservice.port}")
    private int userServicePort;

    @Override
    public List<Long> getStudentChatIdByGroupId(Long groupId) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(userServiceHost, userServicePort)
                .usePlaintext()
                .build();

        var stub = com.project.studentService.StudentServiceGrpc.newBlockingStub(channel);

        var request = com.project.studentService.StudentServiceOuterClass.GroupRequest
                .newBuilder()
                .setGroupId(groupId)
                .build();
        List<Long> ids = new ArrayList<>();
        try {
            var response = stub.getStudentsChatIdByGroupId(request);
            while (response.hasNext()) {
                var user = response.next();
                ids.add(user.getId());
            }
        } catch (io.grpc.StatusRuntimeException e) {
            log.error(e.getMessage());
        }
        return ids;
    }

    @Override
    public Long getStudentChatIdById(Long studentId) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(userServiceHost, userServicePort)
                .usePlaintext()
                .build();

        var stub = com.project.studentService.StudentServiceGrpc.newBlockingStub(channel);

        var request = com.project.studentService.StudentServiceOuterClass.StudentRequest
                .newBuilder()
                .setStudentId(studentId)
                .build();
        try {
            var response = stub.getUserByStudentId(request);
            return response.getId();
        } catch (io.grpc.StatusRuntimeException e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException("Student with id " + studentId + " does not exist");
        }
    }

    @Override
    public StudentDto getStudentById(Long studentId) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(userServiceHost, userServicePort)
                .usePlaintext()
                .build();

        var stub = com.project.studentService.StudentServiceGrpc.newBlockingStub(channel);

        var request = com.project.studentService.StudentServiceOuterClass.StudentRequest
                .newBuilder()
                .setStudentId(studentId)
                .build();
        try {
            var response = stub.getStudentById(request);
            return new StudentDto(response.getId(), response.getName(), response.getSurname(), response.getPatronymic());
        } catch (io.grpc.StatusRuntimeException e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException("Student with id " + studentId + " does not exist");
        }
    }

    @Override
    public GroupDto getGroupById(Long groupId) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(userServiceHost, userServicePort)
                .usePlaintext()
                .build();

        var stub = com.project.groupService.GroupServiceGrpc.newBlockingStub(channel);

        var request = com.project.groupService.GroupServiceOuterClass.GroupRequest
                .newBuilder()
                .setGroupId(groupId)
                .build();
        try {
            var response = stub.getGroupByGroupId(request);
            return new GroupDto(response.getId(), response.getTitle());
        } catch (io.grpc.StatusRuntimeException e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException("Group with id " + groupId + " does not exist");
        }
    }

    @Override
    public StudentDto getStudentByUserId(Long userId) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(userServiceHost, userServicePort)
                .usePlaintext()
                .build();

        var stub = StudentServiceGrpc.newBlockingStub(channel);

        var request = StudentServiceOuterClass.UserRequest
                .newBuilder()
                .setId(userId)
                .build();
        try {
            var response = stub.getStudentByUserId(request);
            return new StudentDto(response.getId(), response.getName(), response.getSurname(), response.getPatronymic());
        } catch (io.grpc.StatusRuntimeException e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException("User with id " + userId + " does not exist");
        }
    }
}
