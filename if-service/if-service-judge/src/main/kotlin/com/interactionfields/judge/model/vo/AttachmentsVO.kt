package com.interactionfields.judge.model.vo

import com.interactionfields.common.domain.AttachmentStatus
import com.interactionfields.common.domain.AttachmentType
import java.time.LocalDateTime

data class AttachmentsVO(
    var uuid: String? = null,
    var meetingUUID: String? = null,
    var type: AttachmentType? = null,
    var status: AttachmentStatus? = null,
    var createAt: LocalDateTime? = null,
    var endAt: LocalDateTime? = null,
)
