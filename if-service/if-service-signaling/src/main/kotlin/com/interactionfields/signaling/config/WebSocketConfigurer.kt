package com.interactionfields.signaling.config

import com.interactionfields.signaling.socket.WebRTCHandler
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

/**
 * Web socket configuration.
 *
 * @author Ashinch
 * @date 2021/08/31
 */
@Configuration
@EnableWebSocket
class WebSocketConfigurer(private val handshakeInterceptor: HandshakeInterceptor) : WebSocketConfigurer {

    /**
     * Register WebSocket handlers.
     */
    override fun registerWebSocketHandlers(webSocketHandlerRegistry: WebSocketHandlerRegistry) {
        webSocketHandlerRegistry
            .addHandler(WebRTCHandler(), "/webrtc")
            .setAllowedOrigins("*")
            .addInterceptors(handshakeInterceptor)
    }
}
