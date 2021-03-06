package com.interactionfields.common.repository

import com.interactionfields.common.domain.AttachmentType
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

    object Enum {

        const val LANGUAGE_JAVA = 1
        const val LANGUAGE_PYTHON2 = 2
        const val LANGUAGE_PYTHON3 = 3
        const val LANGUAGE_JAVASCRIPT = 4
        const val LANGUAGE_C = 5
        const val LANGUAGE_CPP = 6
    }
}
