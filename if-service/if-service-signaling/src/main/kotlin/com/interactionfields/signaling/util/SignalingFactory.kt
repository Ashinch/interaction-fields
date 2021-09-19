package com.interactionfields.signaling.util

import com.interactionfields.signaling.model.Signaling

object SignalingFactory {

    const val JUDGE_RESULT_RECEIVE = "judge_result_receive"

    fun create(event: String, data: Any?) =
        Signaling(event, data)
}
