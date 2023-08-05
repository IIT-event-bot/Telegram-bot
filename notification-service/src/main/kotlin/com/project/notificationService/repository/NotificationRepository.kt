package com.project.notificationService.repository

import com.project.notificationService.models.Notification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

interface NotificationRepository : JpaRepository<Notification, Long> {
    @Transactional
    fun getAllBySendTimeBetween(from: LocalDateTime, to: LocalDateTime): List<Notification>
}