package com.interactionfields.judge.service

import com.interactionfields.common.domain.Attachment
import com.interactionfields.common.domain.AttachmentType
import com.interactionfields.common.extension.ObjectExt.copyFrom
import com.interactionfields.common.extension.uuid36
import com.interactionfields.common.mq.RabbitMQExchanges
import com.interactionfields.common.mq.RabbitMQExt.defaultConvertAndSend
import com.interactionfields.common.mq.RabbitMQRoutingKeys
import com.interactionfields.common.repository.AttachmentRepository
import com.interactionfields.common.repository.AttachmentRepository.attachments
import com.interactionfields.common.repository.MeetingRepository.meetings
import com.interactionfields.judge.model.vo.AttachmentsVO
import com.interactionfields.judge.model.vo.RecordVO
import mu.KotlinLogging
import org.ktorm.database.Database
import org.ktorm.dsl.desc
import org.ktorm.dsl.eq
import org.ktorm.entity.*
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import kotlin.math.ceil

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
    fun commit(meetingUUID: String, typeID: Int, binary: String): Boolean {
        val attachment = Attachment().apply {
            uuid = uuid36
            this.meetingUUID = meetingUUID
            this.binary = binary.toByteArray()
            type = AttachmentType().apply { id = typeID }
            createAt = LocalDateTime.now()
        }
        if (db.attachments.add(attachment) <= 0) return false
        rabbitTemplate.defaultConvertAndSend(
            RabbitMQExchanges.JUDGE,
            RabbitMQRoutingKeys.MEETING_JUDGE_COMMIT,
            attachment,
            mapOf("meetingUUID" to meetingUUID)
        )
        return true
    }

    /**
     * Get commit [RecordVO] in reverse order of creation time.
     */
    fun getRecord(meetingUUID: String, drop: Int, take: Int): RecordVO =
        RecordVO().apply {
            records = db.attachments
                .filter { it.meetingUUID eq meetingUUID}
                .sortedBy { it.createAt.desc() }
                .drop(drop)
                .take(take)
                .map { AttachmentsVO().copyFrom(it) }
            total = ceil(db.attachments
                .filter { it.meetingUUID eq meetingUUID }
                .totalRecords / take.toDouble()).toInt()
        }

    /**
     * Get the binary from the [attachmentUUID].
     */
    fun getBinary(attachmentUUID: String): String? =
        db.attachments.find { it.uuid eq attachmentUUID }?.binary?.let { String(it) }

    /**
     * Get the result from the [attachmentUUID].
     */
    fun getResult(attachmentUUID: String): String? =
        db.attachments.find { it.uuid eq attachmentUUID }?.result?.let { String(it) }
}
