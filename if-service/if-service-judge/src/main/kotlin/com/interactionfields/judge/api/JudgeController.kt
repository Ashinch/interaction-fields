package com.interactionfields.judge.api

import com.interactionfields.auth.common.util.HasAuthority
import com.interactionfields.common.response.R
import com.interactionfields.judge.model.param.CommitParam
import com.interactionfields.judge.service.JudgeService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@Validated
@RestController
@RequestMapping
class JudgeController(private val judgeService: JudgeService) {

    @PostMapping("/commit")
    @PreAuthorize(HasAuthority.USER)
    fun commit(@Valid commitParam: CommitParam): R {
        return R.judge(judgeService.commit(commitParam.meetingUUID, commitParam.typeID!!, commitParam.code))
    }

    @PostMapping("/record")
    @PreAuthorize(HasAuthority.USER)
    fun record(meetingUUID: String): R {
        return R.judge(judgeService.record(meetingUUID))
    }
}
