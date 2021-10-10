package com.interactionfields.common.repository

import com.interactionfields.common.domain.Attachment
import com.interactionfields.common.domain.AttachmentStatus
import com.interactionfields.common.domain.AttachmentType
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.*
import java.time.LocalDateTime

/**
 * The mapper of database for [Attachment] domain.
 *
 * @author Ashinch
 * @date 2021/09/12
 */
object AttachmentRepository : Table<Attachment>("tb_attachment") {

    val id = int("id").primaryKey().bindTo { it.id }
    val uuid = varchar("uuid").bindTo { it.uuid }
    val meetingUUID = varchar("meeting_uuid").bindTo { it.meetingUUID }
    val binary = blob("binary").bindTo { it.binary }
    val type = int("type_id").references(AttachmentTypeRepository) { it.type }
    val createAt = datetime("create_at").bindTo { it.createAt }
    val cpuTime = int("cpu_time").bindTo { it.cpuTime }
    val realTime = int("real_time").bindTo { it.realTime }
    val memory = long("memory").bindTo { it.memory }
    val status = int("status_id").references(AttachmentStatusRepository) { it.status }
    val result = blob("result").bindTo { it.result }

    /**
     * Return a default entity sequence of [AttachmentRepository].
     */
    val Database.attachments get() = this.sequenceOf(AttachmentRepository)
}
