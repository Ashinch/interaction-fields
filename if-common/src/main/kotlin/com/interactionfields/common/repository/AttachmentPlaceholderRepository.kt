package com.interactionfields.common.repository

import com.interactionfields.common.domain.AttachmentPlaceholder
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.blob
import org.ktorm.schema.int

/**
 * The mapper of database for [AttachmentPlaceholder] domain.
 *
 * @author Ashinch
 * @date 2021/10/22
 */
object AttachmentPlaceholderRepository : Table<AttachmentPlaceholder>("dict_attachment_placeholder") {

    val id = int("id").primaryKey().bindTo { it.id }
    val typeID = int("type_id").bindTo { it.typeID }
    val placeholder = blob("placeholder").bindTo { it.placeholder }

    /**
     * Return a default entity sequence of [AttachmentPlaceholderRepository].
     */
    val Database.attachmentPlaceholders get() = this.sequenceOf(AttachmentPlaceholderRepository)
}
