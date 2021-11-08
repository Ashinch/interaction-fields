package com.interactionfields.signaling.service

import com.interactionfields.common.domain.Meeting
import com.interactionfields.common.domain.User
import com.interactionfields.common.domain.UserMeeting
import com.interactionfields.common.repository.MeetingRepository
import com.interactionfields.common.repository.MeetingRepository.meetings
import com.interactionfields.common.repository.UserMeetingRepository.userMeetings
import com.interactionfields.common.repository.UserRepository.users
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.dsl.isNull
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class StoreService(private val db: Database) {

    fun getMeeting(code: String): Meeting? =
        db.meetings.find { (it.code eq code).and(it.endAt.isNull()) }

    fun getUser(uuid: String): User? =
        db.users.find { it.uuid eq uuid }

    fun onJoin(userUUID: String, meetingUUID: String, document: ByteArray) {
        val userMeeting = db.userMeetings.find {
            (it.userUUID eq userUUID).and(it.meetingUUID eq meetingUUID)
        }
        if (userMeeting == null) {
            db.userMeetings.add(UserMeeting().apply {
                this.userUUID = userUUID
                this.meetingUUID = meetingUUID
                doc = document
                joinAt = LocalDateTime.now()
            })
        } else {
            userMeeting.doc = document
            userMeeting.flushChanges()
        }
    }

    fun onQuit(userUUID: String, meetingUUID: String, document: ByteArray, note: ByteArray?) {
        db.userMeetings.find {
            (it.userUUID eq userUUID).and(it.meetingUUID eq meetingUUID)
        }?.let {
            it.doc = document
            it.note = note
            it.quitAt = LocalDateTime.now()
            it.flushChanges()
        }
    }
}
