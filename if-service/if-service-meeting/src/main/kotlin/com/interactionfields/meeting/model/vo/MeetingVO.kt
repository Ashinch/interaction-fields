package com.interactionfields.meeting.model.vo

import java.time.LocalDateTime

data class MeetingVO(
    var uuid: String? = null,
    var title: String? = null,
    var code: String? = null,
    var creatorUUID: String? = null,
    var createdAt: LocalDateTime? = null
)
