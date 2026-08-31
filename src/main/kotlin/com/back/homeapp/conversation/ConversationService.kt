package com.back.homeapp.conversation

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@Service
class ConversationService(
    private val conversationRepository: ConversationRepository,
) {
    fun create(request: ConversationRequest): ConversationResponse {
        val conversation =
            conversationRepository.save(
                Conversation(
                    name = request.name,
                    timestamp = Instant.ofEpochMilli(request.timestamp),
                ),
            )

        return conversation.toResponse()
    }

    fun findAll(): List<ConversationResponse> =
        conversationRepository
            .findAll()
            .sortedBy { it.timestamp.toEpochMilli() }
            .map { it.toResponse() }

    fun findById(id: Long): ConversationResponse =
        conversationRepository
            .findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation $id not found") }
            .toResponse()

    fun update(
        id: Long,
        request: ConversationRequest,
    ): ConversationResponse {
        val room =
            conversationRepository
                .findById(id)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation $id not found") }

        room.name = request.name
        room.timestamp = Instant.ofEpochMilli(request.timestamp)

        return conversationRepository.save(room).toResponse()
    }

    fun delete(id: Long): ConversationResponse {
        val room =
            conversationRepository
                .findById(id)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation $id not found") }

        val response = room.toResponse()
        conversationRepository.delete(room)

        return response
    }
}
