package com.interactionfields.signaling.signal

import com.interactionfields.signaling.ot.Operation

data class OperationSignal(
    val event: String? = null,
    val data: Operation? = null
)
