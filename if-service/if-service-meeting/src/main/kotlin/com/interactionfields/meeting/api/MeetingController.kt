package com.interactionfields.meeting.api

import com.interactionfields.auth.common.util.HasAuthority
import com.interactionfields.auth.common.util.contextAuthPrincipal
import com.interactionfields.common.response.R
import com.interactionfields.meeting.model.dto.CreateMeetingDTO
import com.interactionfields.meeting.model.param.CreateMeetingParam
import com.interactionfields.meeting.service.MeetingService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping
class MeetingController(private val meetingService: MeetingService) {

    @PostMapping("/create")
    @PreAuthorize(HasAuthority.USER)
    fun create(createMeetingParam: CreateMeetingParam): R {
        val createMeetingDTO = CreateMeetingDTO(
            createMeetingParam.title.ifEmpty { "${contextAuthPrincipal.username}的会议" },
            contextAuthPrincipal.getUuid()!!
        )
        return R.judge(meetingService.create(createMeetingDTO))
    }

    @GetMapping("/statusByCode/{code}")
    @PreAuthorize(HasAuthority.USER)
    fun codeStatus(@PathVariable code: String): R {
        return R.judge(meetingService.getStatusByCode(code))
    }

    @PostMapping("/join")
    @PreAuthorize(HasAuthority.USER)
    fun join(): R {
        return R.judge(meetingService.create(CreateMeetingDTO("", "")), "create error")
    }
}
