package com.interactionfields.signaling.model

import com.interactionfields.signaling.ot.Operation

data class OperationSignaling(
    val event: String? = null,
    val data: Operation? = null
)
