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
    var createAt: LocalDateTime
    var cpuTime: Int?
    var realTime: Int?
    var memory: Long?
    var status: AttachmentStatus?
    var result: ByteArray?
}
