package com.interactionfields.signaling.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.listener.RedisMessageListenerContainer

/**
 * RedisListenerConfiguration.kt.
 *
 * @author Ashinch
 * @date 2021/11/10
 */
@Configuration
class RedisListenerConfiguration {

    @Bean
    fun container(connectionFactory: RedisConnectionFactory): RedisMessageListenerContainer =
        RedisMessageListenerContainer().apply {
            setConnectionFactory(connectionFactory)
        }
}
