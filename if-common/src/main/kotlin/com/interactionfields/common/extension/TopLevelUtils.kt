package com.interactionfields.common.extension

import java.util.*

/**
 * Top-level function.
 *
 * @author Ashinch
 * @date 2021/08/31
 */

private var chars = arrayOf(
    "a", "b", "c", "d", "e", "f",
    "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
    "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
    "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
    "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
    "W", "X", "Y", "Z"
)

private fun uuidX(digit: Int): String {
    val shortBuffer = StringBuffer()
    val uuid = uuid32
    for (i in 0 until digit) {
        val x = uuid.substring(i * 4, i * 4 + 4).toInt(16)
        shortBuffer.append(chars[x % 0x3E])
    }
    return shortBuffer.toString()
}

val uuid6: String get() = uuidX(6)
val uuid6U: String get() = uuid6.uppercase(Locale.getDefault())
val uuid8: String get() = uuidX(8)
val uuid8U: String get() = uuid8.uppercase(Locale.getDefault())
val uuid36: String get() = UUID.randomUUID().toString()
val uuid36U: String get() = uuid36.uppercase(Locale.getDefault())
val uuid32: String get() = uuid36.replace("-", "")
val uuid32U: String get() = uuid32.uppercase(Locale.getDefault())
