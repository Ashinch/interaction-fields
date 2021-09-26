package com.interactionfields.judge.model.param

import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

class CommitParam(
    @field:NotEmpty
    val meetingUUID: String = "",

    @field:NotNull
    val typeID: Int?,

    @field:NotEmpty
    val code: String = "",
)
