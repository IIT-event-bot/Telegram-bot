package com.project.notificationService.models

import com.fasterxml.jackson.annotation.JsonFormat
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    var sendTime: LocalDateTime?,

    @Column(name = "event_id")
    val eventId: Long?
)
