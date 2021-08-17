package com.interactionfields.meeting.api

import com.interactionfields.auth.common.util.HasAuthority
import com.interactionfields.auth.common.util.contextAuthId
import com.interactionfields.common.domain.Meeting
import com.interactionfields.common.response.R
import com.interactionfields.meeting.service.MeetingService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@Validated
@RestController
@RequestMapping
class MeetingController(private val meetingService: MeetingService) {
    @PostMapping("/create")
    @PreAuthorize(HasAuthority.USER)
    fun create(): R {
//        val meeting = Meeting()
//        meeting.creatorId = 1
//        meeting.createdAt = LocalDateTime.now()
//        return R.success(meetingService.create(meeting))
        return R.judge(contextAuthId)
    }

    @PostMapping("/join")
    @PreAuthorize(HasAuthority.USER)
    fun join(): R {
        val meeting = Meeting()
        meeting.creatorUUID = "1"
        meeting.createdAt = LocalDateTime.now()
        return R.judge(meetingService.create(meeting), "create error")
    }
}
