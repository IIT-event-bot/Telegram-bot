package com.project.notificationService.service

import com.project.notificationService.models.Notification
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Service
import java.time.LocalTime

@Service
@Slf4j
@RequiredArgsConstructor
class NotificationServiceImpl : NotificationService {
    override fun saveNotification(notification: Notification) {
        throw RuntimeException("Not implemented method")
    }

    override fun getNotificationBeforeTime(time: LocalTime): List<Notification> {
        throw RuntimeException("Not implemented method")
    }
}