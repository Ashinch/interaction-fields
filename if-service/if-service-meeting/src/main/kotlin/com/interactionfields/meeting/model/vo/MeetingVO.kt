package com.interactionfields.meeting.model.vo

import java.time.LocalDateTime

data class MeetingVO(
    var uuid: String? = null,
    var creatorUUID: String? = null,
    var title: String? = null,
    var doc: String? = null,
    var note: String? = null,
    var joinAt: LocalDateTime? = null,
    var quitAt: LocalDateTime? = null
)
