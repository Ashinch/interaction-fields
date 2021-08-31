package com.interactionfields.signaling.config

import com.interactionfields.signaling.socket.WebSocketHandler
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
     * Register Web socket handlers.
     */
    override fun registerWebSocketHandlers(webSocketHandlerRegistry: WebSocketHandlerRegistry) {
        webSocketHandlerRegistry
            //添加myHandler消息处理对象，和websocket访问地址
            .addHandler(WebSocketHandler(), "/games")
            //设置允许跨域访问
            .setAllowedOrigins("*")
            //添加拦截器可实现用户链接前进行权限校验等操作
            .addInterceptors(handshakeInterceptor)
    }
}
