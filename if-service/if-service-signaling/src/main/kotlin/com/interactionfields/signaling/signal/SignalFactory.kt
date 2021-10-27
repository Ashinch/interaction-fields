package com.interactionfields.signaling.signal

import com.interactionfields.signaling.signal.Signal

object SignalFactory {

    fun create(event: String, data: Any?) =
        Signal(event, data)
}
