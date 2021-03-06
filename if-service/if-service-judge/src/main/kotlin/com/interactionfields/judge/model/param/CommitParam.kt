package com.interactionfields.judge.model.param

import javax.validation.constraints.Min
import javax.validation.constraints.NotEmpty

class CommitParam(
    @field:NotEmpty
    val meetingUUID: String = "",

    @field:Min(1)
    val typeID: Int = 0,

    @field:NotEmpty
    val code: String = "",
)
