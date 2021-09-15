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
    var type: Int
    var result: ByteArray
    var status: Int
    var createAt: LocalDateTime
    var endAt: LocalDateTime
}
