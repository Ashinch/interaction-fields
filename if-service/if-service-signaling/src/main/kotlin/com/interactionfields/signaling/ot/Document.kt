package com.interactionfields.signaling.ot

import java.util.concurrent.CopyOnWriteArrayList

data class Document(
    var content: String,
    var operations: CopyOnWriteArrayList<TextOperation>
)
