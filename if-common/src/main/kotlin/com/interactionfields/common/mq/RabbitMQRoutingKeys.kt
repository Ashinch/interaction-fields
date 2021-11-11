package com.interactionfields.common.mq

/**
 * The constants of Rabbit MQ's routing key.
 *
 * @author Ashinch
 * @date 2021/09/16
 */
object RabbitMQRoutingKeys {

    const val JUDGE_COMMIT = "judge.commit"
    const val JUDGE_RESULT = "judge.result"

    const val MEETING_CLOSE = "meeting.close"
}
