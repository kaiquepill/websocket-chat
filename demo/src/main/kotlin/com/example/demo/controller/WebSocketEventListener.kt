package com.example.demo.controller

import com.example.demo.model.ChatMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionConnectedEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent


@Component
class WebSocketEventListener {
    @Autowired
    private val messagingTemplate: SimpMessageSendingOperations? = null

    @EventListener
    fun handleWebSocketConnectListener(event: SessionConnectedEvent?) {
        logger.info("Received a new web socket connection")
    }

    @EventListener
    fun handleWebSocketDisconnectListener(event: SessionDisconnectEvent) {
        val headerAccessor = StompHeaderAccessor.wrap(event.message)
        val username = headerAccessor.sessionAttributes!!["username"] as String?
        if (username != null) {
            logger.info("User Disconnected : $username")
            val chatMessage = ChatMessage()
            chatMessage.type = ChatMessage.MessageType.LEAVE
            chatMessage.sender = username
            messagingTemplate!!.convertAndSend("/topic/public", chatMessage)
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(WebSocketEventListener::class.java)
    }
}