package com.project.scheduleService.config;

import com.project.scheduleService.ScheduleServiceGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcServerConfig {
    @Value("${grpc.scheduleservice.port}")
    private int grpcPort;

    @Bean
    public Server grpcServer(ScheduleServiceGrpc.ScheduleServiceImplBase scheduleService) {
        return ServerBuilder
                .forPort(grpcPort)
                .addService(scheduleService)
                .build();
    }
}
