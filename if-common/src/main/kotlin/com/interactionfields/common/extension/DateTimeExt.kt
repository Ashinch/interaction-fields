package com.interactionfields.common.extension

import java.text.SimpleDateFormat
import java.util.*

/**
 * Utilities about the [Date].
 *
 * @author Ashinch
 * @date 2021/07/31
 */
object DateTimeExt {

    const val YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss"
    const val YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm"
    const val YYYY_MM_DD = "yyyy-MM-dd"
    const val YYYY_MM = "yyyy-MM"
    const val YYYY = "yyyy"

    /**
     * Returns a date-time [String] format from a [Date] object.
     */
    fun Date.toString(pattern: String? = null): String =
        SimpleDateFormat((pattern ?: YYYY_MM_DD_HH_MM_SS)).format(this)

    /**
     * Returns a [Date] object parsed from a date-time [String].
     */
    fun String.toDate(pattern: String? = null): Date =
        SimpleDateFormat((pattern ?: YYYY_MM_DD_HH_MM_SS)).parse(this)
}
