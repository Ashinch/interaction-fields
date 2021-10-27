package com.interactionfields.signaling.signal

data class JoinSignal(
    var uuid: String? = null,
    var username: String? = null,
    var mobile: String? = null,
    var email: String? = null,
)
