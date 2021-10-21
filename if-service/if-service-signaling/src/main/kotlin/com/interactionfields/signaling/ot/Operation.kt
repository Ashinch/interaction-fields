package com.interactionfields.signaling.ot

import java.util.concurrent.atomic.AtomicInteger

data class Operation(
    val op: List<Any>?= null,
    val version: Int?=null,
)
