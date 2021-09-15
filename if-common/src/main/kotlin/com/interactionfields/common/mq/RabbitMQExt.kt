package com.interactionfields.common.mq

import com.interactionfields.common.extension.uuid36
import com.rabbitmq.client.Channel
import org.springframework.amqp.core.MessagePostProcessor
import org.springframework.amqp.rabbit.connection.CorrelationData
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.AmqpHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.MessageHeaders
import org.springframework.messaging.support.MessageBuilder

/**
 * Utilities about the Rabbit MQ.
 *
 * @author Ashinch
 * @date 2021/09/16
 */
object RabbitMQExt {

    /**
     * Acknowledge one or several received messages by default.
     */
    fun Channel.defaultAck(msg: Message<Any>) =
        this.basicAck(msg.headers[AmqpHeaders.DELIVERY_TAG] as Long, false)

    /**
     * Convert and send one messages by default.
     */
    fun RabbitTemplate.defaultConvertAndSend(
        exchange: String,
        routingKey: String,
        payload: Any?,
        headers: Map<String, Any>?
    ) = this.convertAndSend(
        exchange,
        routingKey,
        MessageBuilder.createMessage(payload, MessageHeaders(headers)),
        MessagePostProcessor { message -> return@MessagePostProcessor message },
        CorrelationData(uuid36)
    )
}
