package com.back.homeapp.conversation

import com.back.homeapp.message.Message
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "conversations")
class Conversation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(name = "name", nullable = false)
    var name: String = "",
    @Column(name = "timestamp", nullable = false)
    var timestamp: Instant = Instant.now(),
    @OneToMany(mappedBy = "conversation", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var messages: MutableList<Message> = mutableListOf(),
) {
    fun toResponse() =
        ConversationResponse(
            id = id,
            name = name,
            timestamp = timestamp.toEpochMilli(),
        )
}
