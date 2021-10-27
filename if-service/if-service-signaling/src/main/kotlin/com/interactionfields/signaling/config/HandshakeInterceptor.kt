package com.interactionfields.signaling.config

import com.interactionfields.auth.common.util.contextAuthPrincipal
import com.interactionfields.common.domain.User
import com.interactionfields.signaling.extension.SessionExt
import com.interactionfields.signaling.service.StoreService
import mu.KotlinLogging
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
class HandshakeInterceptor(private val storeService: StoreService) : HandshakeInterceptor {

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

        // Check code
        val meeting = storeService.getMeeting(code) ?: return false

        // Check uuid
        val user = storeService.getUser(uuid) ?: return false

        attributes[SessionExt.MEETING_UUID] = meeting.uuid
        attributes[SessionExt.USER] = user
        // Verify the invitation code
        logger.info { "$uuid: Handshake $code meeting" }
        return true
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
