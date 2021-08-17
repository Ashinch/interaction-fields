package com.interactionfields.user.model.param

import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty

class UserParam(
    @field:NotEmpty
    val username: String = "",

    @field:NotEmpty
    val password: String = "",

    @field:NotEmpty
    val mobile: String = "",

    @field:NotEmpty
    @field:Email
    val email: String = "",
)
