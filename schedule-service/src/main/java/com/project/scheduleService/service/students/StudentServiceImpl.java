package com.project.scheduleService.service.students;

import com.project.scheduleService.models.dto.UserDto;
import com.project.scheduleService.service.notifications.TelegramNotificationService;
import com.project.studentService.StudentServiceGrpc;
import com.project.studentService.StudentServiceOuterClass;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static io.grpc.Status.UNAVAILABLE;
import static io.grpc.Status.UNKNOWN;

@Service
@Slf4j
public class StudentServiceImpl implements StudentService {
    @Value("${grpc.userservice.host}")
    private String host;

    @Value("${grpc.userservice.port}")
    private int port;

    @Value("${admin.chat.id}")
    private long adminChatId;

    private final TelegramNotificationService errorNotificationService;

    public StudentServiceImpl(@Qualifier("errorTelegramNotificationService") TelegramNotificationService errorNotificationService) {
        this.errorNotificationService = errorNotificationService;
    }

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
                throw new IllegalArgumentException("Group with id '" + groupId + "' does not exists");
            }
            if (e.getStatus().getCode().equals(UNAVAILABLE.getCode())) {
                final String errorMessage = "Group service not available";
                log.error(errorMessage);
                errorNotificationService.sendNotification(
                        adminChatId,
                        "Сервис не доступен!",
                        "Сервис студентов не доступен",
                        LocalDateTime.now());
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
    public UserDto getStudentChatIdById(long studentId) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();

        var stub = com.project.studentService.StudentServiceGrpc.newBlockingStub(channel);

        var request = com.project.studentService.StudentServiceOuterClass.StudentRequest
                .newBuilder()
                .setStudentId(studentId)
                .build();
        try {
            var response = stub.getUserByStudentId(request);
            return new UserDto(response.getId(), response.getUsername());
        } catch (StatusRuntimeException e) {
            if (e.getStatus().equals(UNKNOWN)) {
                throw new IllegalArgumentException("Student with id '" + studentId + "' does not exists");
            }
            if (e.getStatus().getCode().equals(UNAVAILABLE.getCode())) {
                final String errorMessage = "Student service not available";
                log.error(errorMessage);
                errorNotificationService.sendNotification(
                        adminChatId,
                        "Сервис не доступен!",
                        "Сервис студентов не доступен",
                        LocalDateTime.now());
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
