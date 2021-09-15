package com.interactionfields.judge.mq.listener

import com.interactionfields.common.domain.Attachment
import com.interactionfields.common.extension.FileExt.readString
import com.interactionfields.common.extension.ObjectExt.copyFrom
import com.interactionfields.common.mq.RabbitMQExchanges
import com.interactionfields.common.mq.RabbitMQExt.defaultAck
import com.interactionfields.common.mq.RabbitMQExt.defaultConvertAndSend
import com.interactionfields.common.mq.RabbitMQQueues
import com.interactionfields.common.mq.RabbitMQRoutingKeys
import com.interactionfields.common.repository.AttachmentRepository
import com.interactionfields.common.repository.AttachmentRepository.attachments
import com.interactionfields.common.repository.AttachmentStatusRepository.attachmentStatus
import com.interactionfields.common.repository.AttachmentTypeRepository.attachmentType
import com.interactionfields.judge.model.vo.CommitResultVO
import com.rabbitmq.client.Channel
import mu.KotlinLogging
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import org.ktorm.entity.update
import org.springframework.amqp.core.ExchangeTypes
import org.springframework.amqp.rabbit.annotation.*
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.cglib.beans.BeanMap
import org.springframework.messaging.Message
import org.springframework.stereotype.Component
import java.io.File
import java.time.Duration
import java.time.LocalDateTime

@Component
class JudgeCommitListener(
    private val rabbitTemplate: RabbitTemplate,
    private val db: Database,
) {

    private val logger = KotlinLogging.logger {}

    /**
     * Consume an [RabbitMQRoutingKeys.MEETING_JUDGE_COMMIT] message,
     * receive an attachment, call compile.sh to compile and run it,
     * and send the [CommitResultVO] to
     * the [RabbitMQRoutingKeys.MEETING_JUDGE_RESULT] consumer.
     */
    @RabbitHandler
    @RabbitListener(
        bindings = [QueueBinding(
            value = Queue(value = RabbitMQQueues.JUDGE_COMMIT, durable = Exchange.TRUE),
            exchange = Exchange(
                name = RabbitMQExchanges.JUDGE,
                durable = Exchange.TRUE,
                type = ExchangeTypes.TOPIC,
                ignoreDeclarationExceptions = Exchange.TRUE
            ),
            key = [RabbitMQRoutingKeys.MEETING_JUDGE_COMMIT]
        )]
    )
    fun onMessage(msg: Message<Any>, channel: Channel) {
        // Acknowledge the message and send result to the WebSocket
        channel.defaultAck(msg)
        logger.info { "Messages are received: $msg" }

        val attachment = msg.payload as Attachment
        var status = AttachmentRepository.StatusEnum.FAILURE
        var result = ""
        try {
            val path = "/Users/ash/Desktop/commit/${attachment.uuid}"
            // TODO: 待商榷
            File(path).mkdir()
            File("$path/Main.java").writeBytes(attachment.binary)
            // Execute the compile script
            val process = Runtime.getRuntime().exec(arrayOf("/Users/ash/Desktop/compile.sh", "$path/Main.java"))
            when (process.waitFor()) {
                0 -> {
                    status = AttachmentRepository.StatusEnum.SUCCESS
                    result = process.inputStream.readString()
                }
                else -> {
                    status = AttachmentRepository.StatusEnum.FAILURE
                    result = process.errorStream.readString()
                }
            }
            process.destroy()
        } catch (e: Exception) {
            result = e.message.toString()
        } finally {
            // Update attachment
            db.attachments.update(attachment.apply {
                this.result = result.toByteArray()
                this.status = status
                endAt = LocalDateTime.now()
            })
            // Send the result to the consumer
            rabbitTemplate.defaultConvertAndSend(
                RabbitMQExchanges.JUDGE,
                RabbitMQRoutingKeys.MEETING_JUDGE_RESULT,
                // TODO: 直接脑淤血
                BeanMap.create(CommitResultVO().apply {
                    uuid = attachment.uuid
                    type = db.attachmentType.find { it.id eq attachment.type }!!.run { mapOf("id" to id, "name" to name) }
                    this.result = result
                    this.status = db.attachmentStatus.find { it.id eq attachment.status }!!.run { mapOf("id" to id, "name" to name) }
                    elapsedTime = Duration.between(attachment.createAt, attachment.endAt).toMillis()
                }).toMap(),
                mapOf("code" to msg.headers["code"]!!)
            )
        }
    }
}
