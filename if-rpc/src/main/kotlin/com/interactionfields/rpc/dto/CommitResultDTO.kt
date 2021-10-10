package com.interactionfields.rpc.dto

import java.io.Serializable

data class CommitResultDTO(
    var cpuTime: Int? = null,
    var realTime: Int? = null,
    var memory: Long? = null,
    var statusCode: Int? = null,
    var result: String? = null,
) : Serializable
