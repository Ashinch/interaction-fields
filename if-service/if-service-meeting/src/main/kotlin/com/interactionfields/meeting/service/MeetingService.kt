package com.interactionfields.meeting.service

import com.alibaba.cloud.nacos.NacosDiscoveryProperties
import com.alibaba.cloud.nacos.NacosServiceManager
import com.interactionfields.common.domain.Meeting
import com.interactionfields.common.extension.ObjectExt.copyFrom
import com.interactionfields.common.extension.uuid6U
import com.interactionfields.common.repository.MeetingRepository.meetings
import com.interactionfields.meeting.model.dto.CreateMeetingDTO
import com.interactionfields.meeting.model.vo.MeetingStatusVO
import com.interactionfields.meeting.model.vo.MeetingVO
import mu.KotlinLogging
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.dsl.isNull
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.springframework.stereotype.Service
import org.springframework.util.Assert
import java.time.LocalDateTime
import java.util.*
import javax.tools.JavaCompiler

@Service
//@Transactional(rollbackFor = [Exception::class])
class MeetingService(
    private val db: Database,
    private val nacosServiceManager: NacosServiceManager,
    private val nacosDiscoveryProperties: NacosDiscoveryProperties
) {
    private val logger = KotlinLogging.logger {}

    /**
     * Create a meeting and return the invitation code.
     */
    fun create(createMeetingDTO: CreateMeetingDTO): MeetingVO {
        // Ensure that the user has no ongoing meetings
        Assert.isNull(
            db.meetings.find { (it.creatorUUID eq createMeetingDTO.creatorUUID).and(it.endedAt.isNull()) },
            "A meeting is in progress"
        )
        val meeting = Meeting().apply {
            uuid = UUID.randomUUID().toString()
            title = createMeetingDTO.title
            code = generateInviteCode()
            creatorUUID = createMeetingDTO.creatorUUID
            createdAt = LocalDateTime.now()
        }
        Assert.isTrue(db.meetings.add(meeting) > 0, "Create meeting error")
        return MeetingVO().copyFrom(meeting)
    }

    fun codeStatus(code: String): MeetingStatusVO {
        val instances = nacosServiceManager
            .getNamingService(nacosDiscoveryProperties.nacosProperties)
            .selectInstances("signaling-service", true)[0]
        Assert.isTrue(!instances?.ip.isNullOrEmpty(), "Signaling-service is not found")
        val meeting = db.meetings.find { (it.code eq code).and(it.endedAt.isNull()) }
        Assert.notNull(meeting, "Meeting code: $code is does not exist")
        return MeetingStatusVO().copyFrom(meeting!!).apply {
            ip = instances.ip
            port = instances.port
        }
    }


    /**
     * Generated a 6-digit meeting invitation code. Invitation codes cannot
     * be the same as those for an ongoing meeting.
     */
    private fun generateInviteCode(): String {
        // Try up to five times
        for (i in 0..4) {
            val code = uuid6U
            if (db.meetings.find { (it.code eq code).and(it.endedAt.isNull()) } == null) {
                return code
            }
        }
        throw IllegalArgumentException("Error generate invitation code")
    }
}
