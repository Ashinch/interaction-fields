package com.interactionfields.judge.model.param

import javax.validation.constraints.Min
import javax.validation.constraints.NotEmpty

class RecordParam(
    @field:NotEmpty
    val meetingUUID: String = "",

    @field:Min(1)
    val pageNum: Int = 0,

    @field:Min(1)
    val pageSize: Int = 0,
)
