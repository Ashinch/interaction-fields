package com.interactionfields.meeting.api

import com.interactionfields.auth.common.util.HasAuthority
import com.interactionfields.auth.common.util.contextAuthPrincipal
import com.interactionfields.common.response.R
import com.interactionfields.meeting.model.dto.CreateMeetingDTO
import com.interactionfields.meeting.model.param.CreateMeetingParam
import com.interactionfields.meeting.model.param.RecordParam
import com.interactionfields.meeting.service.MeetingService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

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

    @PostMapping("/close")
    @PreAuthorize(HasAuthority.USER)
    fun close(): R {
        return R.judge(meetingService.close(contextAuthPrincipal.getUuid()!!))
    }

    @PostMapping("/statusByCode")
    @PreAuthorize(HasAuthority.USER)
    fun statusByCode(code: String): R {
        return R.judge(meetingService.getStatusByCode(code))
    }

    @PostMapping("/statusByUser")
    @PreAuthorize(HasAuthority.USER)
    fun statusByUser(): R {
        return R.success(meetingService.getStatusByUser())
    }

    @PostMapping("/record")
    @PreAuthorize(HasAuthority.USER)
    fun record(@Valid recordParam: RecordParam): R =
        R.judge(meetingService.getRecord(
            recordParam.onlyCreator,
            recordParam.word,
            recordParam.pageNum * recordParam.pageSize - recordParam.pageSize,
            recordParam.pageSize
        ))
}
