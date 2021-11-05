package com.interactionfields.common.repository

import com.interactionfields.common.domain.UserMeeting
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.*

/**
 * The mapper of database for [UserMeeting] domain.
 *
 * @author Ashinch
 * @date 2021/10/27
 */
object UserMeetingRepository : Table<UserMeeting>("tb_user_meeting") {

    val id = int("id").primaryKey().bindTo { it.id }
    val userUUID = varchar("user_uuid").bindTo { it.userUUID }
    val meetingUUID = varchar("meeting_uuid").bindTo { it.meetingUUID }
    var doc = blob("doc").bindTo { it.doc }
    var note = blob("note").bindTo { it.note }
    var joinAt = datetime("join_at").bindTo { it.joinAt }
    var quitAt = datetime("quit_at").bindTo { it.quitAt }

    /**
     * Return a default entity sequence of [UserMeetingRepository].
     */
    val Database.userMeetings get() = this.sequenceOf(UserMeetingRepository)
}
