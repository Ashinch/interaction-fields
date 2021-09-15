package com.interactionfields.common.repository

import com.interactionfields.common.domain.AttachmentStatus
import com.interactionfields.common.domain.AttachmentType
import com.interactionfields.common.domain.Role
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

/**
 * The mapper of database for [AttachmentType] domain.
 *
 * @author Ashinch
 * @date 2021/09/16
 */
object AttachmentTypeRepository : Table<AttachmentType>("dict_attachment_type") {

    val id = int("id").primaryKey().bindTo { it.id }
    val name = varchar("name").bindTo { it.name }

    /**
     * Return a default entity sequence of [AttachmentTypeRepository].
     */
    val Database.attachmentType get() = this.sequenceOf(AttachmentTypeRepository)
}
