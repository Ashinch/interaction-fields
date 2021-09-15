package com.interactionfields.signaling.model

data class Signaling(
    val event: String? = null,
    val data: Any? = null,
) {

    companion object {

        fun createJudgeResultReceive(data: Any) = Signaling("judge_result_receive", data)
    }
}
