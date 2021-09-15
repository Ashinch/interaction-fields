package com.interactionfields.judge.model.vo

data class CommitResultVO(
    var uuid: String? = null,
    var type: Map<String, Any>? = null,
    var status: Map<String, Any>? = null,
    var elapsedTime: Long? = null,
    var result: String? = null,
)
