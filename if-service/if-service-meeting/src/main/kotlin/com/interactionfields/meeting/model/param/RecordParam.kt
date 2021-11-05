package com.interactionfields.meeting.model.param

import javax.validation.constraints.Min

class RecordParam(
    val onlyCreator: Boolean = false,

    val word: String = "",

    @field:Min(1)
    val pageNum: Int = 0,

    @field:Min(1)
    val pageSize: Int = 0,
)
