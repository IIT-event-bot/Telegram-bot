package com.project.notificationService.service

import com.project.notificationService.models.Notification
import com.project.notificationService.models.NotificationType
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
        log.debug("Check notification on hour")
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
        log.debug("Sending notification...")
    }

//    @Scheduled(cron = "* * * * * *")
    @Transactional
    fun test1() {
//        for (i in 0L..50) {
            notificationQueue.pushNotificationToQueue(
                Notification(
                    0,
                    NotificationType.SYS_INFO,
                    1234,
                    "text",
                    "title",
                    LocalDateTime.now().plusMinutes(5),
                    null
                )
            )
//        }
        log.debug("Add from test 1")
    }

//    @Scheduled(cron = "* * * * * *")
    @Transactional
    fun test2() {
//        for (i in 0L..50) {
            notificationQueue.pushNotificationToQueue(
                Notification(
                    0,
                    NotificationType.SYS_INFO,
                    1234,
                    "text",
                    "title",
                    LocalDateTime.now().plusMinutes(5),
                    null
                )
            )
//        }
        log.debug("Add from test 2")
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }
}
