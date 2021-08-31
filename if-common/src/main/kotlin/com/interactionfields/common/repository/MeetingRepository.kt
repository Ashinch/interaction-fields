package com.interactionfields.common.repository

import com.interactionfields.common.domain.Meeting
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.varchar

/**
 * The mapper of database for [Meeting] domain.
 *
 * @author Ashinch
 * @date 2021/07/22
 */
object MeetingRepository : Table<Meeting>("tb_meeting") {

    val id = int("id").primaryKey().bindTo { it.id }
    val uuid = varchar("uuid").bindTo { it.uuid }
    val creatorUUID = varchar("creator_uuid").bindTo { it.creatorUUID }
    val createdAt = datetime("created_at").bindTo { it.createdAt }
    val endedAt = datetime("ended_at").bindTo { it.endedAt }

    /**
     * Return a default entity sequence of [MeetingRepository].
     */
    val Database.meetings get() = this.sequenceOf(MeetingRepository)
}
