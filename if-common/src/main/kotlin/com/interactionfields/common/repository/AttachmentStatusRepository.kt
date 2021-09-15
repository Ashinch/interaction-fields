package com.interactionfields.common.repository

import com.interactionfields.common.domain.AttachmentStatus
import com.interactionfields.common.domain.Role
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

/**
 * The mapper of database for [AttachmentStatus] domain.
 *
 * @author Ashinch
 * @date 2021/09/16
 */
object AttachmentStatusRepository : Table<AttachmentStatus>("dict_attachment_status") {

    val id = int("id").primaryKey().bindTo { it.id }
    val name = varchar("name").bindTo { it.name }

    /**
     * Return a default entity sequence of [AttachmentStatusRepository].
     */
    val Database.attachmentStatus get() = this.sequenceOf(AttachmentStatusRepository)
}
