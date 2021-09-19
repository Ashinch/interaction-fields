package com.interactionfields.common.domain

import org.ktorm.entity.Entity
import java.time.LocalDateTime

/**
 * The [Attachment] domain model.
 *
 * @author Ashinch
 * @date 2021/09/12
 */
interface Attachment : Domain, Entity<Attachment> {

    companion object : Entity.Factory<Attachment>()

    var meetingUUID: String
    var binary: ByteArray
    var type: AttachmentType
    var result: ByteArray
    var status: AttachmentStatus
    var createAt: LocalDateTime
    var endAt: LocalDateTime
}
