package com.interactionfields.common.cache

/**
 * RedisPatterns.kt.
 *
 * @author Ashinch
 * @date 2021/11/11
 */
object RedisPatterns {

    const val E = "__keyevent@*__"
    const val X = "expired"
    const val EX = "$E:$X"
}
