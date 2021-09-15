package com.interactionfields.judge.service

import com.interactionfields.common.domain.Attachment
import com.interactionfields.common.extension.uuid36
import com.interactionfields.common.mq.RabbitMQExchanges
import com.interactionfields.common.mq.RabbitMQExt.defaultConvertAndSend
import com.interactionfields.common.mq.RabbitMQRoutingKeys
import com.interactionfields.common.repository.AttachmentRepository.attachments
import com.interactionfields.common.repository.MeetingRepository.meetings
import mu.KotlinLogging
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class JudgeService(
    private val db: Database,
    private val rabbitTemplate: RabbitTemplate
) {

    private val logger = KotlinLogging.logger {}

    /**
     * Add an attachment and send it to
     * the [RabbitMQRoutingKeys.MEETING_JUDGE_COMMIT] consumer.
     */
    fun commit(binary: String, meetingUUID: String): Boolean {
        val code = db.meetings.find { it.uuid eq meetingUUID }!!.code
        val attachment = Attachment().apply {
            uuid = uuid36
            this.meetingUUID = meetingUUID
            this.binary = binary.toByteArray()
            type = 1
            createAt = LocalDateTime.now()
        }
        if (db.attachments.add(attachment) <= 0) return false
        rabbitTemplate.defaultConvertAndSend(
            RabbitMQExchanges.JUDGE,
            RabbitMQRoutingKeys.MEETING_JUDGE_COMMIT,
            attachment,
            mapOf("code" to code)
        )
        return true
    }
}
