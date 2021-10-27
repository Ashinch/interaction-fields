package com.interactionfields.signaling.signal

import java.io.Serializable

data class Signal(
    var event: String? = null,
    var data: Any? = null,
) : Serializable
