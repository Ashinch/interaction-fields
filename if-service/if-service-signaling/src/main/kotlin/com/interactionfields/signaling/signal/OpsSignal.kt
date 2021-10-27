package com.interactionfields.signaling.signal

data class OpsSignal(
    var version: Int? = null,
    var ops: List<Any>? = null,
)
