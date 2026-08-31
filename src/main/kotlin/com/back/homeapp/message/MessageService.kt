package com.back.homeapp.message

import com.back.homeapp.conversation.ConversationRepository
import com.back.homeapp.room.RoomResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@Service
class MessageService(
    private val messageRepository: MessageRepository,
    private val conversationRepository: ConversationRepository,
) {
    fun create(request: MessageRequest): MessageResponse {
        val conversation =
            conversationRepository
                .findById(request.conversationId)
                .orElseThrow { IllegalArgumentException("Message ${request.conversationId} not found") }

        val message =
            messageRepository.save(
                Message(
                    text = request.text,
                    timestamp = Instant.ofEpochMilli(request.timestamp),
                    response = request.response,
                    conversation = conversation,
                ),
            )

        return message.toResponse()
    }

    fun findAll(conversationId: Long?): List<MessageResponse> {
        return if (conversationId != null) {
            messageRepository.findByConversationId(conversationId)
        } else {
            messageRepository.findAll()
        }
            .sortedBy { it.timestamp.toEpochMilli() }
            .map { it.toResponse() }
    }
    fun findById(id: Long): MessageResponse =
        messageRepository
            .findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Message $id not found") }
            .toResponse()

    fun update(id: Long, request: MessageRequest): MessageResponse {
        val message = messageRepository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Message $id not found") }
        val conversation = conversationRepository.findById(request.conversationId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation $id not found") }

        message.text = request.text
        message.timestamp = Instant.ofEpochMilli(request.timestamp)
        message.response = request.response
        message.conversation = conversation

        return messageRepository.save(message).toResponse()
    }

    fun delete(id: Long): MessageResponse {
        val message = messageRepository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Message $id not found") }

        val response = message.toResponse()
        messageRepository.delete(message)

        return response
    }
}
