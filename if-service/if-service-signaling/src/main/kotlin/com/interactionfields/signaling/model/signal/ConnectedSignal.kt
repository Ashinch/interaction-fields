package com.interactionfields.signaling.model.signal

import java.time.LocalDateTime

data class ConnectedSignal(
    var content: String? = null,
    var version: Int? = null,
    var remind: LocalDateTime? = null,
    var note: String? = null,
    var languageId: Int? = null,
)
