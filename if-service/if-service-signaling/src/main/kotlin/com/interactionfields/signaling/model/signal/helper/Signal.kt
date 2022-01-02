package com.interactionfields.signaling.model.signal.helper

import java.io.Serializable

data class Signal(
    var event: String? = null,
    var data: Any? = null,
) : Serializable
