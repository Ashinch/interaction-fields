package com.interactionfields.signaling.model.dto

import com.interactionfields.signaling.ot.Operation

data class OperationSignalDTO(
    val event: String? = null,
    val data: Operation? = null
)
