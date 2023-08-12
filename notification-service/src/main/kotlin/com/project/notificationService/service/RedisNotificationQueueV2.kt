package com.project.notificationService.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.project.notificationService.models.Notification
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.core.ListOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset

@Service
@Primary
class RedisNotificationQueueV2(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val mapper: ObjectMapper
) : RedisNotificationQueue(redisTemplate, mapper) {
    @Synchronized
    @Transactional
    override fun pushNotificationToQueue(notification: Notification) {
        val time = LocalDateTime.of(
            notification.sendTime!!.toLocalDate(),
            LocalTime.of(notification.sendTime!!.hour, notification.sendTime!!.minute)
        )
        val longTime: Long = time.toEpochSecond(ZoneOffset.of("+5"))
        val clusterName = "$CLUSTER_NAME_PREFIX$CLUSTER_NAME_DELIMITER$longTime"
        val redisList: ListOperations<String, Any> = redisTemplate.opsForList()
        if (redisList.size(QUEUE_NAME) == 0L) {
            redisList.leftPush(QUEUE_NAME, clusterName)
        } else if (redisList.indexOf(QUEUE_NAME, clusterName) == null) {
            pushAndRefreshCluster(clusterName)
        }
        this.insertNotification(notification, clusterName)
    }

    private fun pushAndRefreshCluster(clusterName: String) {
        var clusters: List<String> = redisTemplate.opsForList()
            .range(QUEUE_NAME, 0, -1)!!
            .map { it.toString() }
        clusters += clusterName
        clearQueue()
        clusters = clusters.sortedBy { it.split(CLUSTER_NAME_DELIMITER)[1].toLong() }
        for (cluster in clusters) {
            redisTemplate.opsForList().leftPush(QUEUE_NAME, cluster)
        }
    }

    private fun insertNotification(notification: Notification, clusterName: String) {
        val json = mapper.writeValueAsString(notification)
        redisTemplate.opsForList().leftPush(clusterName, json)
    }

    override fun getNowNotification(): List<Notification> {
        val notification: MutableList<Notification> = arrayListOf()
        val redisQueue: ListOperations<String, Any> = redisTemplate.opsForList()
        val now = LocalDateTime.now(ZoneId.of("Asia/Yekaterinburg"))
        while (true) {
            val cluster: String = redisQueue.rightPop(QUEUE_NAME)?.toString() ?: break
            val time = LocalDateTime.ofEpochSecond(
                cluster.split(CLUSTER_NAME_DELIMITER)[1].toLong(),
                0,
                ZoneOffset.of("+5")
            )
            if (time.isAfter(now)) {
                redisQueue.rightPush(QUEUE_NAME, cluster)
                break
            }
            notification.addAll(getClusterNotification(clusterName = cluster))
        }
        return notification
    }

    private fun getClusterNotification(clusterName: String): List<Notification> {
        val notifications: MutableList<Notification> = arrayListOf()
        while (true) {
            val json = redisTemplate.opsForList().rightPop(clusterName)?.toString() ?: break
            val notification = mapper.readValue<Notification>(json)
            notifications.add(notification)
        }
        return notifications
    }

    companion object {
        const val CLUSTER_NAME_PREFIX = "ntf"
        const val CLUSTER_NAME_DELIMITER = ":"
    }
}