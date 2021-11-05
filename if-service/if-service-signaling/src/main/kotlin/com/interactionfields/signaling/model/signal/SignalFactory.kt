package com.interactionfields.signaling.model.signal

object SignalFactory {

    fun create(event: String, data: Any?) =
        Signal(event, data)
}
