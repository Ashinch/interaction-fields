package com.interactionfields.auth.common.userdetails

import java.time.LocalDateTime

data class UserDetailsDTO(
    var username: String? = null,
    var mobile: String? = null,
    var email: String? = null,
    var joinedAt: LocalDateTime? = null,
    var loggedAt: LocalDateTime? = null,
    var authorities: List<String>? = null
)
