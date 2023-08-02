package com.project.userService.config;

import com.project.userService.UserServiceGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfig {
    @Value("${grpc.userservice.port}")
    private int grpcPort;
    @Bean
    public Server grpcServer(UserServiceGrpc.UserServiceImplBase userService,
                             com.project.groupService.GroupServiceGrpc.GroupServiceImplBase groupService,
                             com.project.studentService.StudentServiceGrpc.StudentServiceImplBase studentService
    ) {
        return ServerBuilder
                .forPort(grpcPort)
                .addService(groupService)
                .addService(studentService)
                .addService(userService)
                .build();
    }
}