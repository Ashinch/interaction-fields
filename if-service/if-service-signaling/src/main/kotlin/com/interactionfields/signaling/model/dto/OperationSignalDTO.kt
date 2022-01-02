package com.interactionfields.signaling.model.dto

import com.interactionfields.signaling.model.param.OperationParam

data class OperationSignalDTO(
    val event: String? = null,
    val data: OperationParam? = null
)
