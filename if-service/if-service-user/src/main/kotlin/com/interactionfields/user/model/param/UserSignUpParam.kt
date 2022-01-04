package com.interactionfields.user.model.param

import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

class UserSignUpParam(
    @field:NotEmpty
    val username: String = "",

    @field:NotEmpty
    @field:Size(min = 2, max = 6)
    val name: String = "",

    @field:NotEmpty
    val password: String = "",

    @field:Pattern(regexp = "^\$|1[3|4|5|7|8][0-9]\\d{8}")
    val mobile: String = "",

    @field:Email
    val email: String = "",
)
