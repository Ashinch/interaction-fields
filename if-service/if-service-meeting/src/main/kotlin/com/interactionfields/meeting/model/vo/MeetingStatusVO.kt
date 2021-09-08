package com.interactionfields.meeting.model.vo

import java.time.LocalDateTime

data class MeetingStatusVO(
    var uuid: String? = null,
    var title: String? = null,
    var code: String? = null,
    var creatorUUID: String? = null,
    var createdAt: LocalDateTime? = null,
    var ip: String? = null,
    var port: Int? = null,
)
