package com.interactionfields.signaling.socket

import mu.KotlinLogging
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger

/**
 * Signaling server of WebRTC.
 *
 * @author Ashinch
 * @date 2021/08/31
 */
class WebRTCHandler : TextWebSocketHandler() {

    private val logger = KotlinLogging.logger {}

    override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>) {
        logger.info { "${session.attributes["uuid"]}: ${message.payload}" }
        sessionPools[session.attributes["code"]]?.forEach {
            if (it.id != session.id) {
                println("${sessionPools[session.attributes["code"]]?.size} it.id: ${it.id}, session.id: ${session.id}, message: ${message}")
                it.sendMessage(message)
            }
        }
    }

    /**
     * 建立连接后应该在sessionPools中存储对应会议code的连接信息，
     */
    override fun afterConnectionEstablished(session: WebSocketSession) {
        // TODO: 重复链接处理？
        if (sessionPools[session.attributes["code"]] == null) {
            sessionPools[session.attributes["code"].toString()] = CopyOnWriteArrayList()
        }
        sessionPools[session.attributes["code"]]!!.add(session)
        addOnlineCount()
        logger.info { "${session.attributes["uuid"]}: Connect ${session.attributes["code"]} meeting, the current number is $onlineNum" }
    }

    override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
        logger.info { "${session.attributes["uuid"]}: Disconnect" }
        sessionPools[session.attributes["code"]]!!.remove(session)
        subOnlineCount()
    }

    companion object {

        // 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
        private val onlineNum = AtomicInteger()

        // concurrent包的线程安全Set，用来存放每个客户端对应的WebSocketServer对象。
        private val sessionPools = ConcurrentHashMap<String, CopyOnWriteArrayList<WebSocketSession>>()

        /**
         * Broadcasts a [message] to the [code] meeting.
         */
        fun sendMessage(code: String, message: WebSocketMessage<*>) {
            println("Broadcast meeting: $code, messages: ${message.payload}")
            sessionPools[code]?.forEach { it.sendMessage(message) }
        }

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
