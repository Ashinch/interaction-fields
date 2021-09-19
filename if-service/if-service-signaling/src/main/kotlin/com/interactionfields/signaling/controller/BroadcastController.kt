package com.interactionfields.signaling.controller

import com.interactionfields.common.extension.JsonExt.toJson
import com.interactionfields.rpc.dto.CommitResultDTO
import com.interactionfields.signaling.socket.WebRTCHandler
import com.interactionfields.signaling.util.SignalingFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.socket.TextMessage

@RestController
@RequestMapping("/broadcast")
class BroadcastController {

    @PostMapping("/judgeResult")
    fun judgeResult(code: String, commitResultDTO: CommitResultDTO) {
        broadcastInternal(code, SignalingFactory.JUDGE_RESULT_RECEIVE, commitResultDTO)
    }

    private fun <T> broadcastInternal(code: String, event: String, data: T?) {
        WebRTCHandler.sendMessage(code, TextMessage(SignalingFactory.create(event, data).toJson()))
    }
}
