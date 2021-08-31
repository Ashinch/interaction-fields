package com.interactionfields.common.extension

import com.interactionfields.common.extension.JsonExt.toJson
import org.springframework.http.MediaType
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Utilities about the Web.
 *
 * @author Ashinch
 * @date 2021/07/31
 */

object WebExt {

    /**
     * Returns [HttpServletRequest] object of current session.
     */
    val request: HttpServletRequest
        get() = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request


    /**
     * Writes the [Any] object as JSON format [String] to the response body.
     */
    @Suppress("DEPRECATION")
    fun HttpServletResponse.write(body: Any) {
        setHeader("Access-Control-Allow-Origin", "*")
        setHeader("Cache-Control", "no-cache")
        contentType = MediaType.APPLICATION_JSON_UTF8_VALUE
        writer.use { it.write(body.toJson()) }
    }
}
