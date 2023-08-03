package com.project.notificationService.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.project.notificationService.models.Notification
import com.project.notificationService.models.NotificationDto
import lombok.extern.slf4j.Slf4j
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneId

@Service
@Slf4j
class RedisNotificationQueue(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val dtoMapper: NotificationDtoConverter
) : NotificationQueueService {
    override fun pushNotificationToQueue(notification: Notification) {
        val mapper: ObjectMapper = jacksonObjectMapper()
        val dto = dtoMapper.map(notification)
        var savedNotification: List<NotificationDto> = redisTemplate.opsForList().range(QUEUE_NAME, 0, -1)
            ?.map { mapper.readValue<NotificationDto>(it.toString()) } as List<NotificationDto>
        savedNotification += (dto)
        savedNotification = savedNotification.sort()
        clearQueue()
        for (n in savedNotification) {
            val json = mapper.writeValueAsString(n)
            redisTemplate.opsForList().leftPush(QUEUE_NAME, json)
        }
    }

    override fun clearQueue() {
        while (redisTemplate.opsForList().rightPop(QUEUE_NAME) != null) {
        }
    }

    override fun getNowNotification(): List<Notification> {
        val notifications = arrayListOf<Notification>()
        if (redisTemplate.opsForList().size(QUEUE_NAME) == 0L) {
            return notifications
        }
        val mapper: ObjectMapper = jacksonObjectMapper()
        val now = LocalDateTime.now(ZoneId.of("Asia/Yekaterinburg"))
        while (true) {
            val notificationJson: String = redisTemplate.opsForList().rightPop(QUEUE_NAME)?.toString() ?: break
            val notificationDto: NotificationDto = mapper.readValue<NotificationDto>(notificationJson)
            val notification = dtoMapper.map(notificationDto)
            if (notification.sendTime!!.isAfterMinute(now)) {
                redisTemplate.opsForList().rightPush(QUEUE_NAME, notificationJson)
                break
            }
            notifications.add(notification)
        }

        return notifications
    }

    companion object {
        const val QUEUE_NAME: String = "notifications"
    }
}

fun List<NotificationDto>.sort(): List<NotificationDto> {
    return this.sortedBy { it.sendTime }
}

fun LocalDateTime.isAfterMinute(dateTime: LocalDateTime): Boolean {
    return this.hour > dateTime.hour || (this.hour == dateTime.hour && this.minute > dateTime.minute)
}