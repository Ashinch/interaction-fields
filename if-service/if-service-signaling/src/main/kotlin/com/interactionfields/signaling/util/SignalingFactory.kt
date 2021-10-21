package com.interactionfields.signaling.util

import com.interactionfields.signaling.model.Signaling

object SignalingFactory {

    const val JUDGE_RESULT_RECEIVE = "judgeResultReceive"
    const val ACK = "ack"
    const val CONNECTED = "connected"
    const val OPERATION = "operation"

    fun create(event: String, data: Any?) =
        Signaling(event, data)
}
