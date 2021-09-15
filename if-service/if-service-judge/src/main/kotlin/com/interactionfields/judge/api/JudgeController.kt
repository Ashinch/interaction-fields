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
    fun exec(@Valid commitParam: CommitParam): R {
        return R.judge(judgeService.commit(commitParam.code, commitParam.meetingUUID))
    }
}
