package com.interactionfields.signaling.model.signal

data class OpsSignal(
    var version: Int? = null,
    var ops: List<Any>? = null,
)
