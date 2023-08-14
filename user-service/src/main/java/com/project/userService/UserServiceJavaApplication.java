package com.project.userService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceJavaApplication.class, args);
    }

}
