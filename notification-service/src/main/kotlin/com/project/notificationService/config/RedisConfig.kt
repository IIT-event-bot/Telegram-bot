package com.project.notificationService.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.StringRedisSerializer


@Configuration
@EnableRedisRepositories
class RedisConfig {
    @Value("\${redis.host}")
    private lateinit var redisHost: String

    @Value("\${redis.port}")
    private var redisPort: Int = 6379

    @Bean
    fun jedisConnectionFactory(): JedisConnectionFactory {
        val configuration = RedisStandaloneConfiguration()
        configuration.hostName = redisHost
        configuration.port = redisPort
        return JedisConnectionFactory(configuration)
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, Any>? {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = jedisConnectionFactory()
        template.keySerializer = StringRedisSerializer()
        return template
    }
}