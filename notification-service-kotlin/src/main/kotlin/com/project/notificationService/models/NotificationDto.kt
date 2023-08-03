package com.project.notificationService.models

import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
data class NotificationDto(
    val id: Long,
    val type: String,
    val chatId: Long,
    val text: String,
    val title: String,
    val sendTime: Long,
    val eventId: Long?
)
