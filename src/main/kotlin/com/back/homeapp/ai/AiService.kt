package com.back.homeapp.ai

import com.back.homeapp.message.MessageResponse
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.stereotype.Service

@Service
class AiService(
    chatClientBuilder: ChatClient.Builder,
    private val aiTools: AiTools,
) {
    private val log = LoggerFactory.getLogger(AiService::class.java)
    private val chatClient = chatClientBuilder.build()

    fun prompt(
        prompt: String,
        messages: List<MessageResponse>,
    ): String =
        try {
            val messageList = mutableListOf<Message>()

            for (message in messages) {
                if (message.response) {
                    messageList.add(AssistantMessage(message.text))
                } else {
                    messageList.add(UserMessage(message.text))
                }
            }

            chatClient
                .prompt()
                .messages(messageList)
                .user(prompt)
                .tools(aiTools)
                .call()
                .content() ?: ""
        } catch (e: Exception) {
            log.error("Gemini call failed: ${e.message}", e.cause)
            throw e
        }
}
