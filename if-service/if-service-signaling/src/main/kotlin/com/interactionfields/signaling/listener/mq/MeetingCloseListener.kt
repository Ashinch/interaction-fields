package com.interactionfields.signaling.listener.mq

import com.interactionfields.common.mq.RabbitMQExchanges
import com.interactionfields.common.mq.RabbitMQExt.defaultAck
import com.interactionfields.common.mq.RabbitMQQueues
import com.interactionfields.common.mq.RabbitMQRoutingKeys
import com.interactionfields.signaling.model.signal.Event
import com.interactionfields.signaling.socket.WebRTCHandler
import com.rabbitmq.client.Channel
import mu.KotlinLogging
import org.springframework.amqp.core.ExchangeTypes
import org.springframework.amqp.rabbit.annotation.*
import org.springframework.messaging.Message
import org.springframework.stereotype.Component

/**
 * MeetingCloseListener.kt.
 *
 * @author Ashinch
 * @date 2021/11/10
 */
@Component
class MeetingCloseListener {

    private val logger = KotlinLogging.logger {}


    @RabbitHandler
    @RabbitListener(
        bindings = [QueueBinding(
            value = Queue(value = RabbitMQQueues.MEETING_CLOSE, durable = Exchange.TRUE),
            exchange = Exchange(
                name = RabbitMQExchanges.MEETING,
                durable = Exchange.TRUE,
                type = ExchangeTypes.TOPIC,
                ignoreDeclarationExceptions = Exchange.TRUE
            ),
            key = [RabbitMQRoutingKeys.MEETING_CLOSE]
        )]
    )
    fun onMessage(msg: Message<Any>, channel: Channel) {
        logger.info { "Messages are received: $msg" }
        WebRTCHandler.broadcast(
            msg.payload.toString(),
            Event.CLOSE,
            null
        )
        channel.defaultAck(msg)
    }
}
