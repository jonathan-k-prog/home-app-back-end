package com.back.homeapp.conversation

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class ConversationRequest(
    @field:Min(value = 1, message = "Id must be greater than 0")
    val id: Long?,
    @field:NotBlank(message = "Conversation name is required")
    @field:Size(max = 100, message = "Conversation name must be 100 characters or less")
    val name: String,
    @field:NotNull(message = "Timestamp is required")
    @field:Positive(message = "Timestamp must be a positive epoch millis value")
    var timestamp: Long,
)
