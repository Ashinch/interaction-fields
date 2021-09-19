package com.interactionfields.judge.service

import com.interactionfields.common.domain.Attachment
import com.interactionfields.common.domain.AttachmentType
import com.interactionfields.common.extension.uuid36
import com.interactionfields.common.mq.RabbitMQExchanges
import com.interactionfields.common.mq.RabbitMQExt.defaultConvertAndSend
import com.interactionfields.common.mq.RabbitMQRoutingKeys
import com.interactionfields.common.repository.AttachmentRepository.attachments
import com.interactionfields.common.repository.AttachmentTypeRepository
import com.interactionfields.common.repository.MeetingRepository.meetings
import mu.KotlinLogging
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.filter
import org.ktorm.entity.find
import org.ktorm.entity.toList
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
        val attachment = Attachment().apply {
            uuid = uuid36
            this.meetingUUID = meetingUUID
            this.binary = binary.toByteArray()
            type = AttachmentType().apply { id = AttachmentTypeRepository.Enum.LANGUAGE_JAVA }
            createAt = LocalDateTime.now()
        }
        if (db.attachments.add(attachment) <= 0) return false
        rabbitTemplate.defaultConvertAndSend(
            RabbitMQExchanges.JUDGE,
            RabbitMQRoutingKeys.MEETING_JUDGE_COMMIT,
            attachment,
            mapOf("code" to db.meetings.find { it.uuid eq meetingUUID }!!.code)
        )
        return true
    }

    fun record(meetingUUID: String): List<Attachment> =
        db.attachments.filter { it.meetingUUID eq meetingUUID }.toList()
}
