package com.back.homeapp.message

import com.back.homeapp.conversation.ConversationResponse

data class MessageResponse(
    val id: Long?,
    val text: String,
    val response: Boolean,
    val timestamp: Long,
    val conversation: ConversationResponse
)
