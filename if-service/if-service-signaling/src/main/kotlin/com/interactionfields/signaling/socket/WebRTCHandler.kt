package com.interactionfields.signaling.socket

import com.interactionfields.common.extension.JsonExt.toJson
import com.interactionfields.common.extension.JsonExt.toObj
import com.interactionfields.common.extension.ObjectExt.copyFrom
import com.interactionfields.signaling.extension.SessionExt.getMeetingUUID
import com.interactionfields.signaling.extension.SessionExt.getUser
import com.interactionfields.signaling.extension.SessionExt.getUserUUID
import com.interactionfields.signaling.ot.Document
import com.interactionfields.signaling.ot.Operation
import com.interactionfields.signaling.ot.TextOperation
import com.interactionfields.signaling.service.StoreService
import com.interactionfields.signaling.signal.*
import mu.KotlinLogging
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * BroadcastController server of WebRTC.
 *
 * @author Ashinch
 * @date 2021/08/31
 */
class WebRTCHandler(private val storeService: StoreService) : TextWebSocketHandler() {

    private val logger = KotlinLogging.logger {}

    override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>) {
        val meetingUUID = session.getMeetingUUID()
        val userUUID = session.getUserUUID()
        logger.info { "In meeting: $meetingUUID, user: $userUUID, send: ${message.payload}" }

        message.payload.toString().toObj(Signal::class.java).let {
            when ((it as Signal).event) {
                Event.ICE_CANDIDATE,
                Event.OFFER,
                Event.ANSWER,
                Event.MIC_CHANGE,
                Event.CAMERA_CHANGE -> {
                    onNormal(session.id, meetingUUID, message)
                }

                Event.LANGUAGE_CHANGE,
                Event.JUDGE_RESULT_RECEIVE,
                Event.EDIT_CHANGE,
                Event.CURSOR_CHANGE -> {
                    onNormal(session.id, meetingUUID, message)
                }

                Event.OPERATION -> {
                    onOperation(session.id, meetingUUID, (message.payload.toString()
                        .toObj(OperationSignal::class.java) as OperationSignal).data!!)
                }

                else -> return
            }
        }
    }

    private fun onNormal(sessionId: String, meetingUUID: String, message: WebSocketMessage<*>) {
        forward(meetingUUID, message, excludeSessionId = sessionId)
    }

    /**
     * Transforms an operation coming from a client against all concurrent
     * operation, applies it to the current document and returns the operation
     * to send to the clients.
     */
    private fun onOperation(sessionId: String, meetingUUID: String, op: Operation) {
        synchronized(docPools[meetingUUID]!!) {
            val doc = docPools[meetingUUID]!!
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

            // StoreService operation
            operation.ops.forEach { doc.operation.ops.add(it) }
            doc.content = operation.apply(doc.content)
            docPools[meetingUUID] = doc

            emit(
                meetingUUID,
                sessionId,
                Event.ACK,
                doc.operation.ops.size
            )
            broadcast(
                meetingUUID,
                Event.OPERATION,
                OpsSignal().apply {
                    version = doc.operation.ops.size
                    ops = operation.ops
                },
                excludeSessionId = sessionId
            )
        }
    }

    /**
     * 建立连接后应该在sessionPools中存储对应会议code的连接信息，
     */
    override fun afterConnectionEstablished(session: WebSocketSession) {
        // TODO: 重复链接处理？
        val meetingUUID = session.getMeetingUUID()
        val userUUID = session.getUserUUID()
        sessionPools[meetingUUID] = sessionPools[meetingUUID] ?: ConcurrentHashMap()
        sessionPools[meetingUUID]!![userUUID] = session
        docPools[meetingUUID] = docPools[meetingUUID] ?: Document("", TextOperation())
        storeService.onJoin(userUUID, meetingUUID, (docPools[meetingUUID] as Document).content.toByteArray())
        emit(
            meetingUUID,
            session.id,
            Event.CONNECTED,
            ConnectedSignal().apply {
                version = docPools[meetingUUID]!!.operation.ops.size
                content = docPools[meetingUUID]!!.content
            }
        )
        broadcast(
            meetingUUID,
            Event.JOIN,
            JoinSignal().copyFrom(session.getUser()),
            excludeSessionId = session.id
        )

        if (sessionPools[meetingUUID]!!.size > 1) {
            sessionPools[meetingUUID]!!.forEach {
                if (it.key != userUUID) {
                    emit(
                        meetingUUID,
                        session.id,
                        Event.JOIN,
                        JoinSignal().copyFrom(storeService.getUser(it.value.getUser().uuid)!!)
                    )
                    return@forEach
                }
            }

        }
        addOnlineCount()
        logger.info { "$userUUID: Connect $meetingUUID meeting, the current number is $onlineNum" }
    }

    override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
        val meetingUUID = session.getMeetingUUID()
        val userUUID = session.getUserUUID()
        val doc = (docPools[meetingUUID] as Document).content.toByteArray()
        logger.info { "$userUUID: Disconnect" }
        sessionPools[meetingUUID]!!.remove(userUUID)
        storeService.onQuit(userUUID, meetingUUID, doc)
        broadcast(
            meetingUUID,
            Event.QUIT,
            JoinSignal().copyFrom(session.getUser()),
            excludeSessionId = session.id
        )
        subOnlineCount()
    }

    companion object {

        private var docPools = ConcurrentHashMap<String, Document>()

        // 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
        private val onlineNum = AtomicInteger()

        // concurrent包的线程安全Set，用来存放每个客户端对应的WebSocketServer对象。
        private val sessionPools = ConcurrentHashMap<String, ConcurrentHashMap<String, WebSocketSession>>()

        private fun sandMessage(
            meetingUUID: String,
            message: WebSocketMessage<*>,
            targetSessionId: String? = null,
            excludeSessionId: String? = null
        ) {
            sessionPools[meetingUUID]?.forEach {
                synchronized(it) {
                    if (targetSessionId != null && it.value.id == targetSessionId) {
                        it.value.sendMessage(message)
                    }
                    if (targetSessionId == null && (excludeSessionId == null || it.value.id != excludeSessionId)) {
                        it.value.sendMessage(message)
                    }
                }
            }
        }

        fun forward(meetingUUID: String, message: WebSocketMessage<*>, excludeSessionId: String? = null) {
            sandMessage(meetingUUID, message, excludeSessionId = excludeSessionId)
        }

        fun broadcast(meetingUUID: String, signal: String, data: Any, excludeSessionId: String? = null) {
            sandMessage(
                meetingUUID,
                TextMessage(SignalFactory.create(signal, data).toJson()),
                excludeSessionId = excludeSessionId
            )
        }

        fun emit(meetingUUID: String, targetSessionId: String, signal: String, data: Any) {
            sandMessage(
                meetingUUID,
                TextMessage(SignalFactory.create(signal, data).toJson()),
                targetSessionId = targetSessionId
            )
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
