package com.interactionfields.signaling.model.signal

data class OperationSignal(
    var version: Int? = null,
    var ops: List<Any>? = null,
)
