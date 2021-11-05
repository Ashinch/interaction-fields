package com.interactionfields.signaling.model.signal

data class JoinSignal(
    var uuid: String? = null,
    var name: String? = null,
    var mobile: String? = null,
    var email: String? = null,
)
