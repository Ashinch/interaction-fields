package com.interactionfields.meeting.service

import com.alibaba.cloud.nacos.NacosDiscoveryProperties
import com.alibaba.cloud.nacos.NacosServiceManager
import com.interactionfields.auth.common.util.contextAuthPrincipal
import com.interactionfields.common.domain.Meeting
import com.interactionfields.common.extension.ObjectExt.copyFrom
import com.interactionfields.common.extension.uuid6U
import com.interactionfields.common.repository.AttachmentTypeRepository.attachmentType
import com.interactionfields.common.repository.MeetingRepository
import com.interactionfields.common.repository.MeetingRepository.meetings
import com.interactionfields.common.repository.UserMeetingRepository
import com.interactionfields.meeting.model.dto.CreateMeetingDTO
import com.interactionfields.meeting.model.vo.MeetingStatusVO
import com.interactionfields.meeting.model.vo.MeetingVO
import com.interactionfields.meeting.model.vo.RecordVO
import mu.KotlinLogging
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.ktorm.entity.toList
import org.springframework.stereotype.Service
import org.springframework.util.Assert
import java.time.LocalDateTime
import java.util.*
import kotlin.math.ceil

@Service
//@Transactional(rollbackFor = [Exception::class])
class MeetingService(
    private val db: Database,
    private val nacosServiceManager: NacosServiceManager,
    private val nacosDiscoveryProperties: NacosDiscoveryProperties,
) {

    private val logger = KotlinLogging.logger {}

    /**
     * Create a meeting and return the invitation code.
     */
    fun create(createMeetingDTO: CreateMeetingDTO): MeetingVO {
        // Ensure that the user has no ongoing meetings
        Assert.isNull(
            db.meetings.find { (it.creatorUUID eq createMeetingDTO.creatorUUID).and(it.endAt.isNull()) },
            "A meeting is in progress"
        )
        val meeting = Meeting().apply {
            uuid = UUID.randomUUID().toString()
            title = createMeetingDTO.title
            code = generateInviteCode()
            creatorUUID = createMeetingDTO.creatorUUID
            createAt = LocalDateTime.now()
        }
        Assert.isTrue(db.meetings.add(meeting) > 0, "Create meeting error")
        return MeetingVO().copyFrom(meeting)
    }

    /**
     * Get the meeting status, signaling server IP address,
     * and port by using the meeting invitation [code].
     */
    fun getStatusByCode(code: String): MeetingStatusVO {
        val instances = nacosServiceManager
            .getNamingService(nacosDiscoveryProperties.nacosProperties)
            .selectInstances("signaling-service", true)[0]
        Assert.isTrue(!instances?.ip.isNullOrEmpty(), "Signaling-service is not found")
        val meeting = db.meetings.find { (it.code eq code).and(it.endAt.isNull()) }
        Assert.notNull(meeting, "Meeting code: $code is does not exist")
        return MeetingStatusVO().copyFrom(meeting!!).apply {
            ip = instances.ip
            port = instances.port
            attachmentType = db.attachmentType.toList()
        }
    }

    /**
     * Get the status of the ongoing meeting by using user uuid.
     */
    fun getStatusByUser(): MeetingStatusVO? {
        val uuid = contextAuthPrincipal.getUuid()!!
        val meeting = db.meetings.find { (it.creatorUUID eq uuid).and(it.endAt.isNull()) } ?: return null
        return MeetingStatusVO().copyFrom(meeting).apply { attachmentType = db.attachmentType.toList() }
    }

    /**
     * Generated a 6-digit meeting invitation code. Invitation
     * codes cannot be the same as those for an ongoing meeting.
     */
    private fun generateInviteCode(): String {
        // Try up to five times
        for (i in 0..4) {
            val code = uuid6U
            if (db.meetings.find { (it.code eq code).and(it.endAt.isNull()) } == null) {
                return code
            }
        }
        throw IllegalArgumentException("Error generate invitation code")
    }

    fun getRecord(onlyCreator: Boolean, word: String, drop: Int, take: Int): RecordVO {
        val contextUUID = contextAuthPrincipal.getUuid()!!
        val condition =
            if (onlyCreator) (MeetingRepository.creatorUUID eq contextUUID)
                .and(UserMeetingRepository.userUUID eq contextUUID)
                .and(MeetingRepository.title like "%$word%")
            else (UserMeetingRepository.userUUID eq contextUUID)
                .and(MeetingRepository.title like "%$word%")
        val leftJoinOnCol = UserMeetingRepository.meetingUUID eq MeetingRepository.uuid

        return RecordVO().apply {
            records = db.from(UserMeetingRepository)
                .leftJoin(MeetingRepository, on = leftJoinOnCol)
                .select(
                    MeetingRepository.uuid,
                    MeetingRepository.creatorUUID,
                    MeetingRepository.title,
                    UserMeetingRepository.doc,
                    UserMeetingRepository.note,
                    UserMeetingRepository.joinAt,
                    UserMeetingRepository.quitAt,
                )
                .where(condition)
                .limit(drop, take)
                .orderBy(UserMeetingRepository.joinAt.desc())
                .map {
                    MeetingVO().apply {
                        uuid = it[MeetingRepository.uuid]
                        creatorUUID = it[MeetingRepository.creatorUUID]
                        title = it[MeetingRepository.title]
                        doc = String(it[UserMeetingRepository.doc] ?: ByteArray(0))
                        note = String(it[UserMeetingRepository.note] ?: ByteArray(0))
                        joinAt = it[UserMeetingRepository.joinAt]
                        quitAt = it[UserMeetingRepository.quitAt]
                    }
                }
            total = ceil(
                db.from(UserMeetingRepository)
                    .leftJoin(MeetingRepository, on = leftJoinOnCol)
                    .select(MeetingRepository.uuid)
                    .where(condition)
                    .limit(drop, take)
                    .totalRecords / take.toDouble()
            ).toInt()
        }
    }
}
