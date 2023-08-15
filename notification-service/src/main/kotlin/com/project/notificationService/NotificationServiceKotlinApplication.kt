package com.project.notificationService

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
//@EnableDiscoveryClient
class NotificationServiceKotlinApplication

fun main(args: Array<String>) {
    runApplication<NotificationServiceKotlinApplication>(*args)
}
