package com.interactionfields.signaling.socket

import com.interactionfields.common.extension.JsonExt.toJson
import com.interactionfields.common.extension.JsonExt.toObj
import com.interactionfields.signaling.model.ConnectedSignaling
import com.interactionfields.signaling.model.OperationSignaling
import com.interactionfields.signaling.model.OpsSignaling
import com.interactionfields.signaling.model.Signaling
import com.interactionfields.signaling.ot.Document
import com.interactionfields.signaling.ot.Operation
import com.interactionfields.signaling.ot.TextOperation
import com.interactionfields.signaling.util.SignalingFactory
import mu.KotlinLogging
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger

/**
 * BroadcastController server of WebRTC.
 *
 * @author Ashinch
 * @date 2021/08/31
 */
class WebRTCHandler : TextWebSocketHandler() {

    private val logger = KotlinLogging.logger {}

    override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>) {
        logger.info { "${session.attributes["uuid"]}: ${message.payload}" }
        message.payload.toString().toObj(Signaling::class.java).let {
            when ((it as Signaling).event) {
                "iceCandidate", "offer", "answer", "micChange", "cameraChange" ->
                    onNormal(session.id, session.attributes["code"].toString(), message)
                "languageChange", "judgeResultReceive", "editChange", "cursorChange" ->
                    onNormal(session.id, session.attributes["code"].toString(), message)
                "operation" ->
                    onOperation(session.id, session.attributes["code"].toString(), (message.payload.toString()
                        .toObj(OperationSignaling::class.java) as OperationSignaling).data!!)
                else -> return
            }
        }
    }

    private fun onNormal(sessionId: String, code: String, message: WebSocketMessage<*>) {
        broadcast(code, message, sessionId)
    }

    /**
     * Transforms an operation coming from a client against all concurrent
     * operation, applies it to the current document and returns the operation
     * to send to the clients.
     */
    private fun onOperation(sessionId: String, code: String, op: Operation) {
        synchronized(docPools[code]!!) {
            val doc = docPools[code]!!
            if (op.version!! < 0 || doc.operation.ops.size < op.version) {
                logger.error { "operation revision not in history" }
                return
            }
            println("op.op: ${op.op}, op.version: ${op.version}, doc.operation.ops: ${doc.operation.ops}")

            // Find all operations that the client didn't know of when it sent the
            // operation ...
            val concurrentOperations = doc.operation.ops.slice(op.version until doc.operation.ops.size)
            println("concurrentOperations: $concurrentOperations")

            // ... and transform the operation against all these operations ...
            var operation = TextOperation(op.op)
            concurrentOperations.forEach {
                operation = TextOperation.transform(operation, TextOperation(listOf(it)))[0]
            }
            println("operation.ops: ${operation.ops}")

            // Store operation
            operation.ops.forEach { doc.operation.ops.add(it) }
            doc.content = operation.apply(doc.content)
            docPools[code] = doc

            emit(
                code, sessionId,
                TextMessage(SignalingFactory.create(SignalingFactory.ACK, doc.operation.ops.size).toJson())
            )
            broadcast(code, TextMessage(SignalingFactory.create(
                SignalingFactory.OPERATION,
                OpsSignaling().apply {
                    version = doc.operation.ops.size
                    ops = operation.ops
                }
            ).toJson()), sessionId = sessionId)
        }
    }

    private fun broadcast(code: String, message: WebSocketMessage<*>, sessionId: String? = null) {
        sessionPools[code]?.forEach {
            synchronized(it) {
                if (sessionId == null || it.id != sessionId) {
                    it.sendMessage(message)
                }
            }
        }
    }

    private fun emit(code: String, sessionId: String, message: WebSocketMessage<*>) {
        sessionPools[code]?.forEach {
            synchronized(it) {
                if (it.id == sessionId) {
                    it.sendMessage(message)
                }
            }
        }
    }

    /**
     * 建立连接后应该在sessionPools中存储对应会议code的连接信息，
     */
    override fun afterConnectionEstablished(session: WebSocketSession) {
        // TODO: 重复链接处理？
        val code = session.attributes["code"].toString()
        sessionPools[code] = sessionPools[code] ?: CopyOnWriteArrayList()
        docPools[code] = docPools[code] ?: Document("", TextOperation())
        sessionPools[code]!!.add(session)
        emit(
            code, session.id,
            TextMessage(SignalingFactory.create(
                SignalingFactory.CONNECTED,
                ConnectedSignaling().apply {
                    version = docPools[code]!!.operation.ops.size
                    content = docPools[code]!!.content
                }
            ).toJson())
        )
        addOnlineCount()
        logger.info { "${session.attributes["uuid"]}: Connect ${session.attributes["code"]} meeting, the current number is $onlineNum" }
    }

    override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
        logger.info { "${session.attributes["uuid"]}: Disconnect" }
        sessionPools[session.attributes["code"]]!!.remove(session)
        subOnlineCount()
    }

    companion object {

        private var docPools = ConcurrentHashMap<String, Document>()

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
