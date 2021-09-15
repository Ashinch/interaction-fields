package com.interactionfields.common.extension

import java.io.*

/**
 * Utilities about the File.
 *
 * @author Ashinch
 * @date 2021/09/14
 */
object FileExt {

    /**
     * Convert [InputStream] to [String].
     */
    fun InputStream.readString(): String =
        BufferedReader(InputStreamReader(this)).useLines { lines ->
            val results = StringBuilder()
            lines.forEach { results.append(it) }
            results.toString()
        }
}
