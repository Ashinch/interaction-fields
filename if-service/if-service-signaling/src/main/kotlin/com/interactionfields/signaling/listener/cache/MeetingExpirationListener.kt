package com.interactionfields.signaling.listener.cache

import com.interactionfields.common.cache.RedisKeys
import com.interactionfields.common.cache.RedisPatterns
import com.interactionfields.common.repository.MeetingRepository.meetings
import mu.KotlinLogging
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.dsl.isNull
import org.ktorm.entity.find
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * MeetingExpirationListener.kt.
 *
 * @author Ashinch
 * @date 2021/11/11
 */
@Component
class MeetingExpirationListener(private val db: Database, container: RedisMessageListenerContainer) :
    KeyExpirationEventMessageListener(container) {

    private val logger = KotlinLogging.logger {}

    override fun onMessage(message: Message, pattern: ByteArray?) {
        val expiredKey: String = message.toString()
        val eventPattern: String = pattern?.let { String(it) } ?: ""
        if (expiredKey.startsWith(RedisKeys.MEETING_CLOSE) && eventPattern == RedisPatterns.EX) {
            val meetingUUID = expiredKey.substring(RedisKeys.MEETING_CLOSE.length)
            val now = LocalDateTime.now()
            db.meetings.find { (it.uuid eq meetingUUID).and(it.endAt.isNull()) }?.let {
                it.endAt = now
                it.flushChanges()
            }
            logger.info { "Meeting closed: $meetingUUID, at $now" }
        }
    }
}
