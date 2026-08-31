package com.back.homeapp.message

data class MessageRequest(
    val id: Long?,
    val text: String,
    val response: Boolean,
    val timestamp: Long,
    val conversationId: Long,
)
