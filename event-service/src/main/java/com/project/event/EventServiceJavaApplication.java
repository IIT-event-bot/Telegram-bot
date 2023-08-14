package com.project.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class EventServiceJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventServiceJavaApplication.class, args);
    }

}
