package com.back.homeapp.message

import org.springframework.data.jpa.repository.JpaRepository

interface MessageRepository : JpaRepository<Message, Long> {
    fun findByConversationId(conversationId: Long): List<Message>
}
