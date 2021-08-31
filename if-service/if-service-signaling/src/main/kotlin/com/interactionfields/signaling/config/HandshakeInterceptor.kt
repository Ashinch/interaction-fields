package com.interactionfields.signaling.config

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
class HandshakeInterceptor : HandshakeInterceptor {

    /**
     * If false is returned before handshake, the link is not established.
     */
    override fun beforeHandshake(
        request: ServerHttpRequest, response: ServerHttpResponse,
        wsHandler: WebSocketHandler, attributes: MutableMap<String, Any>
    ): Boolean {
        //将用户id放入socket处理器的会话(WebSocketSession)中
        val serverHttpRequest = request as ServletServerHttpRequest
        //获取参数
        val meetingId = serverHttpRequest.servletRequest.getParameter("meetingId")
        attributes["meetingId"] = meetingId
        //可以在此处进行权限验证，当用户权限验证通过后，进行握手成功操作，验证失败返回false
        if (meetingId == "123") {
            println("握手失败.....")
            return false
        }
        println("开始握手。。。。。。。")
        return true
    }

    /**
     * The event after the handshake.
     */
    override fun afterHandshake(
        request: ServerHttpRequest, response: ServerHttpResponse,
        wsHandler: WebSocketHandler, exception: Exception?
    ) {
        println("握手成功啦。。。。。。")
    }
}
