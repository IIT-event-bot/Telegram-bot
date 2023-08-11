package com.project.notificationService.repository

import com.project.notificationService.models.Notification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

interface NotificationRepository : JpaRepository<Notification, Long> {
    @Transactional
    fun getAllBySendTimeBetween(from: LocalDateTime, to: LocalDateTime): List<Notification>

    @Transactional
    @Modifying
    @Query(value = """
        update notifications
        set is_send = true
        where id = :#{#notificationId}
    """, nativeQuery = true)
    fun setSendNotification(notificationId: Long)

    @Transactional
    @Modifying
    @Query(value = """
        select *
        from notifications
        where is_send = false
          and send_time < now()
    """, nativeQuery = true)
    fun getNotSendingBeforeNotifications(): List<Notification>
}