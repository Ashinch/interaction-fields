package com.interactionfields.signaling.config

import com.interactionfields.signaling.service.StoreService
import com.interactionfields.signaling.socket.WebRTCHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean

/**
 * Web socket configuration.
 *
 * @author Ashinch
 * @date 2021/08/31
 */
@Configuration
@EnableWebSocket
class WebSocketConfigurer(
    private val handshakeInterceptor: HandshakeInterceptor,
    private val storeService: StoreService
) : WebSocketConfigurer {

    private val MAX_MESSAGE_SIZE = 20 * 1024
    private val MAX_IDLE = 60 * 60 * 1000

    @Bean
    fun createServletServerContainerFactoryBean(): ServletServerContainerFactoryBean {
        val container = ServletServerContainerFactoryBean()
        container.setMaxTextMessageBufferSize(MAX_MESSAGE_SIZE)
        container.setMaxBinaryMessageBufferSize(MAX_MESSAGE_SIZE)
        container.setMaxSessionIdleTimeout(MAX_IDLE.toLong())
        return container
    }

    /**
     * Register WebSocket handlers.
     */
    override fun registerWebSocketHandlers(webSocketHandlerRegistry: WebSocketHandlerRegistry) {
        webSocketHandlerRegistry
            .addHandler(WebRTCHandler(storeService), "/webrtc")
            .setAllowedOrigins("*")
            .addInterceptors(handshakeInterceptor)
    }
}
