package com.interactionfields.signaling.model

import com.interactionfields.signaling.ot.Document
import com.interactionfields.signaling.ot.TextOperation
import org.springframework.web.socket.WebSocketSession
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

data class MeetingDO(

    /**
     * userUUID: [String]
     * session: [WebSocketSession]
     */
    var sessionPool: ConcurrentHashMap<String, WebSocketSession> = ConcurrentHashMap(),
    var notePool: ConcurrentHashMap<String, String> = ConcurrentHashMap(),
    var document: Document = Document("", TextOperation()),
    var remind: LocalDateTime? = null
)
