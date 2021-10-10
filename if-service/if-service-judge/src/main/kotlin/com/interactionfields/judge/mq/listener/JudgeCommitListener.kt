package com.interactionfields.judge.mq.listener

import com.interactionfields.common.domain.Attachment
import com.interactionfields.common.extension.FileExt.del
import com.interactionfields.common.extension.FileExt.mkDir
import com.interactionfields.common.extension.JsonExt.toObj
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
        val hostPath = "${System.getProperty("user.home")}/.interaction-fields/commit/${attachment.uuid}"
        val containerInnerPath = "/commit/${attachment.uuid}"
        var process: Process? = null
        var commitResultDTO = CommitResultDTO()
        try {
            // Write file to the host
            File(hostPath).mkDir()
            File("$hostPath/main").writeBytes(attachment.binary)
            // Copy file from the host to the Docker container
            ProcessBuilder(listOf(
                "/usr/local/bin/docker", "cp",
                hostPath, "if-sandbox:/commit"
            )).start().run {
                waitFor()
                destroy()
            }
            // Call the run script inside the Docker container,
            // We do not need to quote -c arguments when using arrays.
            process = ProcessBuilder(listOf(
                "/usr/local/bin/docker", "exec",
                "if-sandbox", "/bin/sh", "-c",
                "/run.sh ${attachment.type.id} $containerInnerPath/main"
            )).redirectErrorStream(true).start()
            // Run the commit code, and convert result.
            process.waitFor()
            commitResultDTO = String(process.inputStream.readBytes())
                .also { logger.info { "json: \n$it" } }
                .toObj(CommitResultDTO::class.java) as CommitResultDTO
            // Ignore unnecessary path text
            commitResultDTO.apply { result = result?.replace("$containerInnerPath/", "")?.trim() }
            // Save the result
            attachment.apply {
                status = db.attachmentStatus.find { it.id eq commitResultDTO.statusCode!! }!!
                result = commitResultDTO.result?.toByteArray() ?: ByteArray(0)
                cpuTime = commitResultDTO.cpuTime
                realTime = commitResultDTO.realTime
                memory = commitResultDTO.memory
            }
        } catch (e: Exception) {
            e.printStackTrace()
            attachment.apply {
                status = db.attachmentStatus.find { it.id eq AttachmentStatusRepository.Enum.FAILURE }!!
                result = e.message?.toByteArray() ?: ByteArray(0)
            }
        } finally {
            // Destroy the process and delete the generated directory
            process?.destroy()
            // Clean up files in host
            File(hostPath).del()
            // Clean up files in docker container
            ProcessBuilder(listOf(
                "/usr/local/bin/docker", "exec",
                "if-sandbox", "rm", "-rf", containerInnerPath
            )).start().run {
                waitFor()
                destroy()
            }
            // Update attachment
            db.attachments.update(attachment.apply { type = db.attachmentType.find { it.id eq type.id }!! })
            // Send the result to the consumer
            rabbitTemplate.defaultConvertAndSend(
                RabbitMQExchanges.JUDGE,
                RabbitMQRoutingKeys.MEETING_JUDGE_RESULT,
                commitResultDTO,
                mapOf("code" to msg.headers["code"]!!)
            )
        }
    }
}
