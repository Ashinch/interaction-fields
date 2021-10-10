package com.interactionfields.judge.model.vo

import com.interactionfields.common.domain.AttachmentStatus
import com.interactionfields.common.domain.AttachmentType
import java.time.LocalDateTime

data class AttachmentsVO(
    var uuid: String? = null,
    var meetingUUID: String? = null,
    var binary: ByteArray? = null,
    var type: AttachmentType? = null,
    var createAt: LocalDateTime? = null,
    var cpuTime: Int? = null,
    var realTime: Int? = null,
    var memory: Long? = null,
    var status: AttachmentStatus? = null,
    var result: ByteArray? = null,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AttachmentsVO

        if (uuid != other.uuid) return false
        if (meetingUUID != other.meetingUUID) return false
        if (binary != null) {
            if (other.binary == null) return false
            if (!binary.contentEquals(other.binary)) return false
        } else if (other.binary != null) return false
        if (type != other.type) return false
        if (createAt != other.createAt) return false
        if (cpuTime != other.cpuTime) return false
        if (realTime != other.realTime) return false
        if (memory != other.memory) return false
        if (status != other.status) return false
        if (result != null) {
            if (other.result == null) return false
            if (!result.contentEquals(other.result)) return false
        } else if (other.result != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result1 = uuid?.hashCode() ?: 0
        result1 = 31 * result1 + (meetingUUID?.hashCode() ?: 0)
        result1 = 31 * result1 + (binary?.contentHashCode() ?: 0)
        result1 = 31 * result1 + (type?.hashCode() ?: 0)
        result1 = 31 * result1 + (createAt?.hashCode() ?: 0)
        result1 = 31 * result1 + (cpuTime ?: 0)
        result1 = 31 * result1 + (realTime ?: 0)
        result1 = 31 * result1 + (memory?.hashCode() ?: 0)
        result1 = 31 * result1 + (status?.hashCode() ?: 0)
        result1 = 31 * result1 + (result?.contentHashCode() ?: 0)
        return result1
    }
}
