package com.interactionfields.signaling.socket

import org.springframework.web.socket.*
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * Signaling server of WebRTC.
 *
 * @author Ashinch
 * @date 2021/08/31
 */
class WebSocketHandler : TextWebSocketHandler() {

    override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>) {
        println(session.id + ":" + message)
        sessionPools.forEach {
            if (it.key != session.id) {
                it.value.sendMessage(message)
            }
        }
    }

    override fun handlePongMessage(session: WebSocketSession, message: PongMessage) {
        super.handlePongMessage(session, message)
    }

    override fun handleBinaryMessage(session: WebSocketSession, message: BinaryMessage) {
        super.handleBinaryMessage(session, message)
    }

    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        super.handleTransportError(session, exception)
    }

    override fun supportsPartialMessages(): Boolean {
        return super.supportsPartialMessages()
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        super.handleTextMessage(session, message)
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        println("获取到拦截器中用户ID : " + session.id)
        //TODO: 重复链接没有进行处理
        sessionPools[session.id] = session
        addOnlineCount()
        println(session.id + "加入webSocket！当前人数为" + onlineNum)
        session.sendMessage(TextMessage("欢迎连接到ws服务! 当前人数为：$onlineNum"))
    }

    override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
        println("${session.id}断开连接!")
        sessionPools.remove(session.id)
        subOnlineCount()
    }

    companion object {

        //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
        private val onlineNum = AtomicInteger()

        //concurrent包的线程安全Set，用来存放每个客户端对应的WebSocketServer对象。
        private val sessionPools: ConcurrentHashMap<String, WebSocketSession> =
            ConcurrentHashMap<String, WebSocketSession>()

        /**
         * 添加链接人数
         */
        fun addOnlineCount() {
            onlineNum.incrementAndGet()
        }

        /**
         * 移除链接人数
         */
        fun subOnlineCount() {
            onlineNum.decrementAndGet()
        }
    }
}
