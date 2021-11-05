package com.interactionfields.user.model.vo

import java.time.LocalDateTime

data class UserVo(
    var uuid: String?= null,
    var username: String?= null,
    var name: String?= null,
    var mobile: String?= null,
    var email: String?= null,
    var joinAt: LocalDateTime?= null,
    var signUpAt: LocalDateTime?= null,
)
