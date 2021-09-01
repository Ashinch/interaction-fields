package com.interactionfields.signaling.config

import com.interactionfields.auth.common.util.contextAuthPrincipal
import com.interactionfields.common.repository.MeetingRepository.meetings
import mu.KotlinLogging
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.dsl.isNull
import org.ktorm.entity.find
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor

/**
 * Websocket handshake interceptor.
 *
 * @author Ashinch
 * @date 2021/08/31
 */
@Component
class HandshakeInterceptor(private val db: Database) : HandshakeInterceptor {

    private val logger = KotlinLogging.logger {}

    /**
     * If false is returned before handshake, the link is not established.
     */
    override fun beforeHandshake(
        request: ServerHttpRequest, response: ServerHttpResponse,
        wsHandler: WebSocketHandler, attributes: MutableMap<String, Any>
    ): Boolean {
        val serverHttpRequest = request as ServletServerHttpRequest
        val code = serverHttpRequest.servletRequest.getParameter("code")
        val uuid = contextAuthPrincipal.getUuid()!!
        attributes["code"] = code
        attributes["uuid"] = uuid
        // Verify the invitation code
        return (db.meetings.find { (it.code eq code).and(it.endedAt.isNull()) } != null)
            .also { logger.info { "$uuid: Handshake $code meeting is $it" } }
    }

    /**
     * The event after the handshake.
     */
    override fun afterHandshake(
        request: ServerHttpRequest, response: ServerHttpResponse,
        wsHandler: WebSocketHandler, exception: Exception?
    ) {
//        println("握手成功啦。。。。。。")
    }
}
