package com.interactionfields.judge.api

import com.interactionfields.auth.common.util.HasAuthority
import com.interactionfields.common.response.R
import com.interactionfields.judge.model.param.CommitParam
import com.interactionfields.judge.model.param.RecordParam
import com.interactionfields.judge.service.JudgeService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Validated
@RestController
@RequestMapping
class JudgeController(private val judgeService: JudgeService) {

    @PostMapping("/commit")
    @PreAuthorize(HasAuthority.USER)
    fun commit(@Valid commitParam: CommitParam): R =
        R.judge(judgeService.commit(
            commitParam.meetingUUID,
            commitParam.typeID,
            commitParam.code)
        )

    @PostMapping("/record")
    @PreAuthorize(HasAuthority.USER)
    fun record(@Valid recordParam: RecordParam): R =
        R.judge(judgeService.getRecord(
            recordParam.meetingUUID,
            recordParam.pageNum * recordParam.pageSize - recordParam.pageSize,
            recordParam.pageSize
        ))

    @GetMapping("/binary/{attachmentUUID}")
    fun getBinary(@PathVariable attachmentUUID: String): R =
        R.judge(judgeService.getBinary(attachmentUUID))

    @GetMapping("/result/{attachmentUUID}")
    fun getResult(@PathVariable attachmentUUID: String): R =
        R.judge(judgeService.getResult(attachmentUUID))
}
