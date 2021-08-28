package com.interactionfields.user.model.param

import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Null
import javax.validation.constraints.Pattern

class UserSignUpParam(
    @field:NotEmpty
    val username: String = "",

    @field:NotEmpty
    val password: String = "",

    @Null
    @field:Pattern(regexp = "1[3|4|5|7|8][0-9]\\d{8}")
    val mobile: String = "",

    @field:Email
    val email: String = "",
)
