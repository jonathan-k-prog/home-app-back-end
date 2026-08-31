package com.back.homeapp.chatBot

import com.back.homeapp.conversation.ConversationResponse
import com.back.homeapp.message.MessageResponse

data class ChatBotResponse(
    val conversation: ConversationResponse,
    val question: MessageResponse,
    val answer: MessageResponse,
)
