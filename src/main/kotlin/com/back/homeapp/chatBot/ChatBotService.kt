package com.back.homeapp.chatBot

import com.back.homeapp.ai.AiService
import com.back.homeapp.conversation.ConversationRequest
import com.back.homeapp.conversation.ConversationService
import com.back.homeapp.message.MessageRequest
import com.back.homeapp.message.MessageService
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class ChatBotService(
    private val aiService: AiService,
    private val conversationService: ConversationService,
    private val messageService: MessageService,
) {
    fun talk(request: ChatBotRequest): ChatBotResponse {
        try {
            val conversation =
                if (request.conversationId != null) {
                    conversationService.findById(request.conversationId)
                } else {
                    conversationService.create(
                        ConversationRequest(
                            id = null,
                            name = "New Conversation",
                            timestamp = request.timestamp,
                        ),
                    )
                }

            val conversationId = conversation.id ?: 0
            val oldMessages = messageService.findAll(conversationId)

            val questionRequest =
                MessageRequest(
                    id = null,
                    text = request.text,
                    timestamp = request.timestamp,
                    response = false,
                    conversationId = conversationId,
                )
            val question = messageService.create(questionRequest)

            val reply = aiService.prompt(request.text, oldMessages)

            val responseRequest =
                MessageRequest(
                    id = null,
                    text = reply,
                    timestamp = Instant.now().toEpochMilli(),
                    response = true,
                    conversationId = conversationId,
                )
            val answer = messageService.create(responseRequest)

            return ChatBotResponse(conversation = conversation, question = question, answer = answer)
        } catch (e: Exception) {
            throw e
        }
    }
}
