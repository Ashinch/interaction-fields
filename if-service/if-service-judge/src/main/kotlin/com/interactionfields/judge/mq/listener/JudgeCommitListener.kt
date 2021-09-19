package com.interactionfields.judge.mq.listener

import com.interactionfields.common.domain.Attachment
import com.interactionfields.common.extension.ObjectExt.copyFrom
import com.interactionfields.common.mq.RabbitMQExchanges
import com.interactionfields.common.mq.RabbitMQExt.defaultAck
import com.interactionfields.common.mq.RabbitMQExt.defaultConvertAndSend
import com.interactionfields.common.mq.RabbitMQQueues
import com.interactionfields.common.mq.RabbitMQRoutingKeys
import com.interactionfields.common.repository.AttachmentRepository.attachments
import com.interactionfields.common.repository.AttachmentStatusRepository
import com.interactionfields.common.repository.AttachmentStatusRepository.attachmentStatus
import com.interactionfields.common.repository.AttachmentTypeRepository.attachmentType
import com.interactionfields.rpc.dto.CommitResultDTO
import com.rabbitmq.client.Channel
import mu.KotlinLogging
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import org.ktorm.entity.update
import org.springframework.amqp.core.ExchangeTypes
import org.springframework.amqp.rabbit.annotation.*
import org.springframework.amqp.rabbit.core.RabbitTemplate
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
     * and send the [CommitResultDTO] to
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
        var process: Process? = null
        try {
            val path = "/Users/ash/Desktop/commit/${attachment.uuid}"
            // TODO: 待商榷
            File(path).mkdir()
            File("$path/Main.java").writeBytes(attachment.binary)
            // Execute the compile script
            process = ProcessBuilder(listOf("/Users/ash/Desktop/compile.sh", "$path/Main.java"))
                .redirectErrorStream(true)
                .start()
            // Waiting for
            val waitFor = process.waitFor()
            attachment.apply {
                status = db.attachmentStatus.find {
                    it.id eq
                            if (waitFor == 0) AttachmentStatusRepository.Enum.SUCCESS
                            else AttachmentStatusRepository.Enum.FAILURE
                }!!
                result = process.inputStream.readBytes()
                endAt = LocalDateTime.now()
            }
        } catch (e: Exception) {
            attachment.apply {
                status = db.attachmentStatus.find { it.id eq AttachmentStatusRepository.Enum.FAILURE }!!
                result = e.message?.toByteArray() ?: ByteArray(0)
                endAt = LocalDateTime.now()
            }
        } finally {
            process?.destroy()
            // Update attachment
            db.attachments.update(attachment.apply { type = db.attachmentType.find { it.id eq type.id }!! })
            // Send the result to the consumer
            rabbitTemplate.defaultConvertAndSend(
                RabbitMQExchanges.JUDGE,
                RabbitMQRoutingKeys.MEETING_JUDGE_RESULT,
                CommitResultDTO().copyFrom(attachment).apply {
                    elapsedTime = Duration.between(attachment.createAt, attachment.endAt).toMillis()
                    result = String(attachment.result).trim()
                },
                mapOf("code" to msg.headers["code"]!!)
            )
        }
    }
}
