package com.interactionfields.meeting.service

import com.interactionfields.common.domain.Meeting
import com.interactionfields.common.repository.MeetingRepository.meetings
import com.interactionfields.meeting.model.vo.StreamVO
import org.ktorm.database.Database
import org.ktorm.entity.add
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(rollbackFor = [Exception::class])
class MeetingService(private val db: Database) {
    fun create(meeting: Meeting): StreamVO {
        db.meetings.add(meeting)
        return StreamVO("rtmp://127.0.0.1/live", meeting.id.toString())
    }
}
