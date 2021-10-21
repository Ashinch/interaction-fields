package com.interactionfields.signaling.model

import java.io.Serializable

data class Signaling(
    var event: String? = null,
    var data: Any? = null,
) : Serializable
