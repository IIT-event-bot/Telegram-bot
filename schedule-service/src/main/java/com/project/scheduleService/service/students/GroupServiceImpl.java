package com.project.scheduleService.service.students;

import com.project.groupService.GroupServiceGrpc;
import com.project.groupService.GroupServiceOuterClass;
import com.project.scheduleService.models.dto.GroupDto;
import com.project.scheduleService.service.notifications.TelegramNotificationService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static io.grpc.Status.UNAVAILABLE;
import static io.grpc.Status.UNKNOWN;

@Service
@Slf4j
public class GroupServiceImpl implements GroupService {
    @Value("${grpc.userservice.host}")
    private String userServiceHost;
    @Value("${grpc.userservice.port}")
    private int userServicePort;
    @Value("${admin.chat.id}")
    private long adminChatId;
    private final TelegramNotificationService errorNotificationService;

    public GroupServiceImpl(@Qualifier("errorTelegramNotificationService") TelegramNotificationService errorNotificationService) {
        this.errorNotificationService = errorNotificationService;
    }

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
                errorNotificationService.sendNotification(
                        adminChatId,
                        "Сервис не доступен!",
                        "Сервис групп не доступен",
                        LocalDateTime.now());
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
                errorNotificationService.sendNotification(
                        adminChatId,
                        "Сервис не доступен!",
                        "Сервис групп не доступен",
                        LocalDateTime.now());
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
