package com.back.homeapp.message

import com.back.homeapp.conversation.Conversation
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "messages")
class Message(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    var text: String = "",
    @Column(name = "timestamp", nullable = false)
    var timestamp: Instant = Instant.now(),
    @Column(name = "response", nullable = false)
    var response: Boolean = false,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversationId", nullable = false)
    var conversation: Conversation,
) {
    fun toResponse() =
        MessageResponse(
            id = id,
            text = text,
            timestamp = timestamp.toEpochMilli(),
            response = response,
            conversation = conversation.toResponse()
        )
}
