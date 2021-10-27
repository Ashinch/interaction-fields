package com.interactionfields.signaling.extension

import com.interactionfields.common.domain.User
import org.springframework.web.socket.WebSocketSession

object SessionExt {

    const val USER = "user"
    const val MEETING_UUID = "meetingUUID"

    fun WebSocketSession.getUserUUID(): String = getUser().uuid
    fun WebSocketSession.getUser(): User = this.attributes[USER] as User
    fun WebSocketSession.getMeetingUUID(): String = this.attributes[MEETING_UUID].toString()
}
