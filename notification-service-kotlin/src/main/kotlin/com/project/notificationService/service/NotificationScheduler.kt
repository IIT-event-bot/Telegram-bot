package com.project.notificationService.service

import com.project.notificationService.models.Notification
import com.project.notificationService.models.NotificationType
import jakarta.transaction.Transactional
import lombok.RequiredArgsConstructor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.Random
import java.util.concurrent.atomic.AtomicLong

@Service
@RequiredArgsConstructor
@EnableScheduling
class NotificationScheduler(private val notificationQueue: NotificationQueueService) {
    val counter: AtomicLong = AtomicLong()

//    @Scheduled(cron = "0 */1 * * * *")
    @Transactional
    @Synchronized
    fun saveNotificationOnHour() {
        notificationQueue.pushNotificationToQueue(
            Notification(
                counter.incrementAndGet(),
                NotificationType.SYS_INFO,
                1234,
                "text",
                "title",
                LocalDateTime.now().plusMinutes(Random().nextLong(20)),
                null
            )
        )
        log.info("Send notification to queue")
    }

    @Scheduled(cron = "*/30 * * * * *")
    @Transactional
    @Synchronized
    fun sendNotificationOnTime() {
        val notifications = notificationQueue.getNowNotification()
        log.info("Check notification")
        for (notification in notifications) {
            log.info("title: ${notification.title}, chat: ${notification.chatId}, send time: ${notification.sendTime}")
        }
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }
}
