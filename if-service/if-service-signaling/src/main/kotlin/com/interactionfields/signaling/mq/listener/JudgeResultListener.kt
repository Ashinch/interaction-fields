package com.interactionfields.signaling.mq.listener

import com.interactionfields.common.mq.RabbitMQExchanges
import com.interactionfields.common.mq.RabbitMQExt.defaultAck
import com.interactionfields.common.mq.RabbitMQQueues
import com.interactionfields.common.mq.RabbitMQRoutingKeys
import com.interactionfields.signaling.model.signal.Event
import com.interactionfields.signaling.model.signal.Signal
import com.interactionfields.signaling.socket.WebRTCHandler
import com.rabbitmq.client.Channel
import mu.KotlinLogging
import org.springframework.amqp.core.ExchangeTypes
import org.springframework.amqp.rabbit.annotation.*
import org.springframework.messaging.Message
import org.springframework.stereotype.Component

@Component
class JudgeResultListener {

    private val logger = KotlinLogging.logger {}

    /**
     * Consume an [RabbitMQRoutingKeys.MEETING_JUDGE_RESULT] message,
     * receive an [msg], and convert it to a [Signal],
     * and broadcast it to everyone in the meeting.
     */
    @RabbitHandler
    @RabbitListener(
        bindings = [QueueBinding(
            value = Queue(value = RabbitMQQueues.JUDGE_RESULT, durable = Exchange.TRUE),
            exchange = Exchange(
                name = RabbitMQExchanges.JUDGE,
                durable = Exchange.TRUE,
                type = ExchangeTypes.TOPIC,
                ignoreDeclarationExceptions = Exchange.TRUE
            ),
            key = [RabbitMQRoutingKeys.MEETING_JUDGE_RESULT]
        )]
    )
    fun onMessage(msg: Message<Any>, channel: Channel) {
        logger.info { "Messages are received: $msg" }
        WebRTCHandler.broadcast(
            msg.headers["meetingUUID"].toString(),
            Event.JUDGE_RESULT_RECEIVE,
            msg.payload
        )
        channel.defaultAck(msg)
    }
}
