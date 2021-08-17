package com.interactionfields.meeting.model.param

import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty

data class MeetingParam(
    @NotEmpty(message = "username not empty")
    var username: String,
    @NotEmpty(message = "password not empty")
    var password: String,
    var mobile: String,
    @Email(message = "email not is email")
    var email: String,
)
