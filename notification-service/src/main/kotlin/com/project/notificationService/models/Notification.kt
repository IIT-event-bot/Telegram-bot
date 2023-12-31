package com.project.notificationService.models

import jakarta.persistence.*
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import java.time.LocalDateTime

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "notifications")
data class Notification(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long,

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    val type: NotificationType,

    @Column(name = "chat_it", nullable = false)
    val chatId: Long,

    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    val text: String,

    @Column(name = "title", nullable = false)
    val title: String,

    @Column(name = "send_time", nullable = false)
    var sendTime: LocalDateTime?,

    @Column(name = "event_id")
    val eventId: Long?,

    @Column(name = "is_send", nullable = false)
    val isSend: Boolean
)
