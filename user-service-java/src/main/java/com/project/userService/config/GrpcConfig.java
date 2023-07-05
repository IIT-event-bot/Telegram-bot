package com.project.userService.config;

import com.project.userService.UserServiceGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfig {
    @Bean
    public Server grpcServer(UserServiceGrpc.UserServiceImplBase userService,
                             com.project.groupService.GroupServiceGrpc.GroupServiceImplBase groupService,
                             com.project.studentService.StudentServiceGrpc.StudentServiceImplBase studentService
    ) {
        return ServerBuilder
                .forPort(8100)
                .addService(groupService)
                .addService(studentService)
                .addService(userService)
                .build();
    }
}