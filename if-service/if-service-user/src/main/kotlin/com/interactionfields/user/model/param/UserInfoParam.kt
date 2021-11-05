package com.interactionfields.user.model.param

import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Pattern

class UserInfoParam(
    val name: String? = null,

    @field:Email
    val email: String = "",
)
