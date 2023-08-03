package com.project.notificationService.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@EnableScheduling
class NotificationScheduler(
    private val service: NotificationService,
    private val notificationQueue: NotificationQueueService
) {
    @Scheduled(cron = "0 0 */1 * * *")
    @Transactional
    @Synchronized
    fun saveNotificationOnHour() {
        log.info("Check notification on hour")
        val onHourNotification = service.getNotificationBeforeTime(LocalDateTime.now().plusHours(1))
        notificationQueue.pushNotificationsToQueue(onHourNotification)
    }

    @Scheduled(cron = "0 */1 * * * *")
    @Transactional
    @Synchronized
    fun sendNotificationOnTime() {
        val notifications = notificationQueue.getNowNotification()
        for (n in notifications) {
            service.sendNotification(n)
        }
        log.info("Sending notification...")
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }
}
