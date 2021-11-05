package com.interactionfields.common.domain

import org.ktorm.entity.Entity
import java.time.LocalDateTime

/**
 * The [UserMeeting] domain model.
 *
 * @author Ashinch
 * @date 2021/10/27
 */
interface UserMeeting : Entity<UserMeeting> {

    companion object : Entity.Factory<UserMeeting>()

    val id: Int
    var userUUID: String
    var meetingUUID: String
    var doc: ByteArray
    var note: ByteArray?
    var joinAt: LocalDateTime
    var quitAt: LocalDateTime
}
