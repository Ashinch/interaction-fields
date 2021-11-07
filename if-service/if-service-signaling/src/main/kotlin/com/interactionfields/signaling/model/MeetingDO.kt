package com.interactionfields.signaling.model

import com.interactionfields.signaling.ot.Document
import com.interactionfields.signaling.ot.TextOperation
import org.springframework.web.socket.WebSocketSession
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

data class MeetingDO(

    /**
     * userUUID: [String]
     * session: [WebSocketSession]
     */
    var sessionPool: ConcurrentHashMap<String, WebSocketSession> = ConcurrentHashMap(),
    var notePool: ConcurrentHashMap<String, String> = ConcurrentHashMap(),
    var document: Document = Document("", CopyOnWriteArrayList(listOf(TextOperation()))),
    var remind: LocalDateTime? = null,
    var languageId: Int? = 1
)
