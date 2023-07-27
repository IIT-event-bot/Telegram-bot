package com.project.event.services;

import com.project.event.models.GroupDto;
import com.project.event.models.StudentDto;
import com.project.studentService.StudentServiceGrpc;
import com.project.studentService.StudentServiceOuterClass;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static io.grpc.Status.UNAVAILABLE;
import static io.grpc.Status.UNKNOWN;

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
            return ids;
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

    @Override
    public Long getChatIdByStudentId(Long studentId) {
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
        } catch (StatusRuntimeException e) {
            if (e.getStatus().equals(UNKNOWN)) {
                final String errorMessage = "Student with id '" + studentId + "' does not exists";
                log.error(errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }
            if (e.getStatus().getCode().equals(UNAVAILABLE.getCode())) {
                final String errorMessage = "Student service not available";
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
            if (e.getStatus().getCode().equals(UNKNOWN.getCode())) {
                final String errorMessage = "Student with id " + studentId + " does not exist";
                log.error(errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }
            if (e.getStatus().getCode().equals(UNAVAILABLE.getCode())) {
                final String errorMessage = "Student service not available";
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
            if (e.getStatus().getCode().equals(UNKNOWN.getCode())) {
                final String errorMessage = "Group with id " + groupId + " does not exist";
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
            if (e.getStatus().getCode().equals(UNKNOWN.getCode())) {
                final String errorMessage = "User with id " + userId + " does not exist";
                log.error(errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }
            if (e.getStatus().getCode().equals(UNAVAILABLE.getCode())) {
                final String errorMessage = "Student service not available";
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
