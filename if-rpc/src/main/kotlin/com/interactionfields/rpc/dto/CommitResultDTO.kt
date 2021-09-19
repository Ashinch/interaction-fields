package com.interactionfields.rpc.dto

import com.interactionfields.common.domain.AttachmentStatus
import com.interactionfields.common.domain.AttachmentType
import java.io.Serializable

data class CommitResultDTO(
    var uuid: String? = null,
    var type: AttachmentType? = null,
    var status: AttachmentStatus? = null,
    var elapsedTime: Long? = null,
    var result: String? = null,
) : Serializable
