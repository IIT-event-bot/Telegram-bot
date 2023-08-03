package com.project.notificationService.service

import jakarta.transaction.Transactional
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
class NotificationScheduler {
    @Scheduled(cron = "10 */1 * * * *")
    @Transactional
    @Synchronized
    fun sendNotificationOnTime() {
        throw RuntimeException("Not implemented method")
    }
}
