package com.interactionfields.user.model.vo

import java.time.LocalDateTime

data class UserVo(
    var username: String,
    var mobile: String?,
    var email: String?,
    var joinAt: LocalDateTime,
    var signUpAt: LocalDateTime,
)
