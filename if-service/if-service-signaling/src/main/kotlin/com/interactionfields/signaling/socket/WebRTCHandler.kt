package com.interactionfields.signaling.socket

import com.interactionfields.common.extension.JsonExt.toJson
import com.interactionfields.common.extension.JsonExt.toObj
import com.interactionfields.common.extension.ObjectExt.copyFrom
import com.interactionfields.signaling.extension.SessionExt.getMeetingUUID
import com.interactionfields.signaling.extension.SessionExt.getUser
import com.interactionfields.signaling.extension.SessionExt.getUserUUID
import com.interactionfields.signaling.model.MeetingDO
import com.interactionfields.signaling.model.dto.*
import com.interactionfields.signaling.model.signal.*
import com.interactionfields.signaling.ot.Operation
import com.interactionfields.signaling.ot.TextOperation
import com.interactionfields.signaling.service.StoreService
import mu.KotlinLogging
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * BroadcastController server of WebRTC.
 *
 * @author Ashinch
 * @date 2021/08/31
 */
class WebRTCHandler(private val storeService: StoreService) : TextWebSocketHandler() {

    override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>) {
        val meetingUUID = session.getMeetingUUID()
        val userUUID = session.getUserUUID()

        (message.payload.toString().toObj(Signal::class.java) as Signal).let {
            if (it.event != Event.HEARTBEAT) {
                logger.info { "[Receive]\nmeeting: $meetingUUID,\nuser: $userUUID\nsignal: ${it.event}\ndata: ${it.data}\n" }
            }
            when (it.event) {
                Event.ICE_CANDIDATE,
                Event.OFFER,
                Event.ANSWER,
                Event.MIC_CHANGE,
                Event.CAMERA_CHANGE -> {
                    onNormal(session.id, meetingUUID, message)
                }

                Event.JUDGE_RESULT_RECEIVE,
                Event.EDIT_CHANGE,
                Event.CURSOR_CHANGE -> {
                    onNormal(session.id, meetingUUID, message)
                }

                Event.OPERATION -> {
                    onOperation(session.id, meetingUUID, (message.payload.toString()
                        .toObj(OperationSignalDTO::class.java) as OperationSignalDTO).data!!)
                }

                Event.LANGUAGE_CHANGE -> {
                    onLanguage(meetingUUID, session.id, (message.payload.toString()
                        .toObj(LanguageSignalDTO::class.java) as LanguageSignalDTO).data!!)
                }

                Event.REMIND -> {
                    onRemind(meetingUUID, (message.payload.toString()
                        .toObj(RemindSignalDTO::class.java) as RemindSignalDTO).data!!)
                }

                Event.HIDDEN -> {
                    broadcast(meetingUUID, Event.HIDDEN, null)
                }

                Event.NOTE -> {
                    onNote(meetingUUID, userUUID, (message.payload.toString()
                        .toObj(NoteSignalDTO::class.java) as NoteSignalDTO).data!!)
                }

                Event.HEARTBEAT -> {
                    onHeartbeat(meetingUUID, session.id, (message.payload.toString()
                        .toObj(HeartbeatSignalDTO::class.java) as HeartbeatSignalDTO).data!!)
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
        val doc = meetingPool[meetingUUID]!!.document
        if (op.version!! < 0 || doc.operations.size < op.version) {
            logger.error { "operation revision not in history" }
            return
        }

        // Find all operations that the client didn't know of when it sent the
        // operation ...
        val concurrentOperations = CopyOnWriteArrayList(doc.operations.slice(op.version until doc.operations.size))

        // ... and transform the operation against all these operations ...
        var operation = TextOperation(op.op)
        concurrentOperations.forEach {
            operation = TextOperation.transform(operation, it)[0]
        }

        // StoreService operation
        doc.operations.add(operation)
        doc.content = operation.apply(doc.content)
        meetingPool[meetingUUID]!!.document = doc

        emit(
            meetingUUID,
            sessionId,
            Event.ACK,
            doc.operations.size
        )
        broadcast(
            meetingUUID,
            Event.OPERATION,
            OpsSignal().apply {
                version = doc.operations.size
                ops = operation.ops
            },
            excludeSessionId = sessionId
        )
    }

    private fun onRemind(meetingUUID: String, remind: Int) {
        val localDateTime = if (remind > 0) LocalDateTime.now().plusMinutes(remind.toLong()) else null
        meetingPool[meetingUUID]!!.remind = localDateTime
        broadcast(
            meetingUUID,
            Event.REMIND,
            localDateTime
        )
    }

    private fun onLanguage(meetingUUID: String, sessionId: String, languageId: Int) {
        meetingPool[meetingUUID]!!.languageId = languageId
        broadcast(meetingUUID, Event.LANGUAGE_CHANGE, languageId, excludeSessionId = sessionId)
    }

    private fun onNote(meetingUUID: String, userUUID: String, note: String) {
        meetingPool[meetingUUID]!!.notePool[userUUID] = note
    }

    private fun onHeartbeat(meetingUUID: String, sessionId: String, timestamp: Long) {
        emit(meetingUUID, sessionId, Event.HEARTBEAT, System.currentTimeMillis() - timestamp)
    }

    /**
     * 建立连接后应该在sessionPools中存储对应会议code的连接信息，
     */
    override fun afterConnectionEstablished(session: WebSocketSession) {
        // TODO: 重复链接处理？
        val meetingUUID = session.getMeetingUUID()
        val userUUID = session.getUserUUID()

        meetingPool[meetingUUID] = meetingPool[meetingUUID] ?: MeetingDO()
        meetingPool[meetingUUID]!!.sessionPool[userUUID] = ConcurrentWebSocketSessionDecorator(session, 2000, 4096)


        storeService.onJoin(userUUID, meetingUUID, meetingPool[meetingUUID]!!.document.content.toByteArray())

        // Send a connected signal to this session
        emit(
            meetingUUID,
            session.id,
            Event.CONNECTED,
            ConnectedSignal().apply {
                version = meetingPool[meetingUUID]!!.document.operations.size
                content = meetingPool[meetingUUID]!!.document.content
                remind = meetingPool[meetingUUID]!!.remind
                note = meetingPool[meetingUUID]!!.notePool[userUUID]
                languageId = meetingPool[meetingUUID]!!.languageId
            }
        )

        // Broadcast this user information to other sessions
        broadcast(
            meetingUUID,
            Event.JOIN,
            JoinSignal().copyFrom(session.getUser()),
            excludeSessionId = session.id
        )

        // Send other user information to this session
        if (meetingPool[meetingUUID]!!.sessionPool.size > 1) {
            meetingPool[meetingUUID]!!.sessionPool.forEach {
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
        logger.info { "[Connect]\nmeeting: $meetingUUID\nuser: $userUUID\nonline: ${meetingPool[meetingUUID]!!.sessionPool.size}\n" }
    }

    /**
     * Disconnect
     */
    override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
        val meetingUUID = session.getMeetingUUID()
        val userUUID = session.getUserUUID()
        val doc = meetingPool[meetingUUID]!!.document.content.toByteArray()
        val note = meetingPool[meetingUUID]!!.notePool[userUUID]?.toByteArray()
        meetingPool[meetingUUID]!!.sessionPool.remove(userUUID)
        storeService.onQuit(userUUID, meetingUUID, doc, note)
        broadcast(
            meetingUUID,
            Event.QUIT,
            JoinSignal().copyFrom(session.getUser()),
            excludeSessionId = session.id
        )
        logger.info { "[Disconnect]\nmeeting: $meetingUUID\nuser: $userUUID\nonline: ${meetingPool[meetingUUID]!!.sessionPool.size}\n" }
    }

    companion object {

        private val logger = KotlinLogging.logger {}
        private val meetingPool = ConcurrentHashMap<String, MeetingDO>()

        /**
         * Send a [message] to [targetSessionId]
         * or send a [message] to all session but [excludeSessionId].
         */
        private fun sandMessage(
            meetingUUID: String,
            message: WebSocketMessage<*>,
            targetSessionId: String? = null,
            excludeSessionId: String? = null
        ) {
            meetingPool[meetingUUID]!!.sessionPool.forEach {
                if (targetSessionId != null && it.value.id == targetSessionId) {
                    it.value.sendMessage(message)
                }
                if (targetSessionId == null && (excludeSessionId == null || it.value.id != excludeSessionId)) {
                    it.value.sendMessage(message)
                }
            }
        }

        /**
         * Forward a [message] to all session but [excludeSessionId].
         */
        fun forward(meetingUUID: String, message: WebSocketMessage<*>, excludeSessionId: String? = null) {
            logger.info { "[Forward]\nmeeting: $meetingUUID\nsend: ${message.payload}\n" }
            sandMessage(meetingUUID, message, excludeSessionId = excludeSessionId)
        }

        /**
         * Broadcast a [signal] to all session but [excludeSessionId].
         */
        fun broadcast(meetingUUID: String, signal: String, data: Any?, excludeSessionId: String? = null) {
            logger.info { "[Broadcast]\nmeeting: $meetingUUID\nsignal: $signal\nsend: $data\n" }
            sandMessage(
                meetingUUID,
                TextMessage(SignalFactory.create(signal, data).toJson()),
                excludeSessionId = excludeSessionId
            )
        }

        /**
         * Send a [signal] to [targetSessionId].
         */
        fun emit(meetingUUID: String, targetSessionId: String, signal: String, data: Any) {
            if (signal != Event.HEARTBEAT) {
                logger.info { "[Emit]\nmeeting: $meetingUUID\nsignal: $signal\nsend: $data\n" }
            }
            sandMessage(
                meetingUUID,
                TextMessage(SignalFactory.create(signal, data).toJson()),
                targetSessionId = targetSessionId
            )
        }

        fun getOnlineMember(meetingUUID: String): Int =
            meetingPool[meetingUUID]?.sessionPool?.size ?: 0

        fun ensureSingleConnection(meetingUUID: String, userUUID: String) {
            meetingPool[meetingUUID]?.sessionPool?.get(userUUID)?.close(CloseStatus.SERVICE_OVERLOAD)
        }
    }
}
