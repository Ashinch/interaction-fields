package com.interactionfields.common.extension

import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader

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

    /**
     * Delete a file or directory.
     */
    fun File.del() {
        if (this.isFile) {
            delete()
            return
        }
        this.listFiles()?.forEach { it.del() }
        delete()
    }

    fun File.mkDir() {
        val dirArray = this.absolutePath.split("/".toRegex())
        var pathTemp = ""
        for (i in 1 until dirArray.size) {
            pathTemp = "$pathTemp/${dirArray[i]}"
            val newF = File("${dirArray[0]}$pathTemp")
            if (!newF.exists()) {
                val cheatDir: Boolean = newF.mkdir()
                println(cheatDir)
            }
        }
    }
}
