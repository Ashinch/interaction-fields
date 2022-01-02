package com.interactionfields.signaling.model.signal.helper

object SignalFactory {

    fun create(event: String, data: Any?) =
        Signal(event, data)
}
