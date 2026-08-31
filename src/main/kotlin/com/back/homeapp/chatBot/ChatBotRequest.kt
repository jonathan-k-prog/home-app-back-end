package com.back.homeapp.chatBot

data class ChatBotRequest(
    val text: String,
    val timestamp: Long,
    val conversationId: Long?,
)
