package com.interactionfields.judge.model.param

import javax.validation.constraints.NotEmpty

class CommitParam(
    @field:NotEmpty
    val code: String = "",

    @field:NotEmpty
    val meetingUUID: String = "",
)
