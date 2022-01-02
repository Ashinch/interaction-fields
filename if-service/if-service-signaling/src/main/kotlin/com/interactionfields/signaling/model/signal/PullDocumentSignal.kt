package com.interactionfields.signaling.model.signal

data class PullDocumentSignal(
    var content: String? = null,
    var version: Int? = null,
)
