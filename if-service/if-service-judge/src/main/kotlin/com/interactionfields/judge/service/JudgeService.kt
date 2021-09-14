package com.interactionfields.judge.service

import com.interactionfields.common.domain.Attachment
import com.interactionfields.common.extension.uuid36
import com.interactionfields.common.repository.AttachmentRepository.attachments
import mu.KotlinLogging
import org.ktorm.database.Database
import org.ktorm.entity.add
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.time.LocalDateTime

@Service
class JudgeService(private val db: Database) {

    private val logger = KotlinLogging.logger {}

    /**
     * Create a meeting and return the invitation code.
     */
    fun exec(code: String): String? {
        db.attachments.add(Attachment().apply {
            uuid = uuid36
            meetingUUID = uuid36
            binary = code.toByteArray()
            type = 1
            createAt = LocalDateTime.now()
        })
        File("/Users/ash/Desktop/1.java").writeBytes(code.toByteArray())
        var process: Process? = null
        var result: String? = null
        try {
            process = Runtime.getRuntime().exec("javac /Users/ash/Desktop/1.java")
            result = BufferedReader(InputStreamReader(process!!.inputStream)).useLines { lines ->
                val results = StringBuilder()
                lines.forEach { results.append(it) }
                results.toString()
            }

            if (result.isNotEmpty()) {
                return result
            }

            process = Runtime.getRuntime().exec("java /Users/ash/Desktop/1.java")
            result = BufferedReader(InputStreamReader(process!!.inputStream)).useLines { lines ->
                val results = StringBuilder()
                lines.forEach { results.append(it) }
                results.toString()
            }

            if (process!!.waitFor() != 0) {
                println("error")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }
}
