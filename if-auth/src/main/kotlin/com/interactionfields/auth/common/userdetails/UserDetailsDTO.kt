package com.interactionfields.auth.common.userdetails

import java.time.LocalDateTime

data class UserDetailsDTO(
    var uuid: String? = null,
    var username: String? = null,
    var name: String? = null,
    var mobile: String? = null,
    var email: String? = null,
    var joinAt: LocalDateTime? = null,
    var signUpAt: LocalDateTime? = null,
    var authorities: List<String>? = null
)
