package com.interactionfields.user.model.param

import javax.validation.constraints.NotBlank

class ChangePwdParam(
    @field:NotBlank
    val old: String,

    @field:NotBlank
    val new: String,
)
