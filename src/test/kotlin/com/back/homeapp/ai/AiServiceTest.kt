package com.back.homeapp.ai

import com.back.homeapp.conversation.ConversationResponse
import com.back.homeapp.message.MessageResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.UserMessage

@ExtendWith(MockitoExtension::class)
class AiServiceTest {
    @Mock
    private lateinit var builder: ChatClient.Builder

    @Mock
    private lateinit var chatClient: ChatClient

    @Mock
    private lateinit var requestSpec: ChatClient.ChatClientRequestSpec

    @Mock
    private lateinit var callResponseSpec: ChatClient.CallResponseSpec

    @Mock
    private lateinit var aiTools: AiTools

    private lateinit var aiService: AiService

    private val conversation = ConversationResponse(id = 1, name = "Chat", timestamp = 1000L)

    @BeforeEach
    fun setUp() {
        whenever(builder.build()).thenReturn(chatClient)
        aiService = AiService(builder, aiTools)
    }

    private fun message(
        text: String,
        response: Boolean,
    ) = MessageResponse(id = 1, text = text, response = response, timestamp = 1000L, conversation = conversation)

    @Test
    fun `prompt converts history and returns the model reply`() {
        whenever(chatClient.prompt()).thenReturn(requestSpec)
        whenever(requestSpec.messages(any<List<Message>>())).thenReturn(requestSpec)
        whenever(requestSpec.user(eq("What's up"))).thenReturn(requestSpec)
        whenever(requestSpec.tools(aiTools)).thenReturn(requestSpec)
        whenever(requestSpec.call()).thenReturn(callResponseSpec)
        whenever(callResponseSpec.content()).thenReturn("Hello back")

        val history =
            listOf(
                message("Hi", response = false),
                message("Hello", response = true),
            )

        val result = aiService.prompt("What's up", history)

        assertEquals("Hello back", result)

        val messagesCaptor = org.mockito.kotlin.argumentCaptor<List<Message>>()
        org.mockito.kotlin.verify(requestSpec).messages(messagesCaptor.capture())
        val sentMessages = messagesCaptor.firstValue
        assertEquals(2, sentMessages.size)
        assertEquals(UserMessage::class.java, sentMessages[0]::class.java)
        assertEquals(AssistantMessage::class.java, sentMessages[1]::class.java)
    }

    @Test
    fun `prompt returns an empty string when the model has no content`() {
        whenever(chatClient.prompt()).thenReturn(requestSpec)
        whenever(requestSpec.messages(any<List<Message>>())).thenReturn(requestSpec)
        whenever(requestSpec.user(any<String>())).thenReturn(requestSpec)
        whenever(requestSpec.tools(aiTools)).thenReturn(requestSpec)
        whenever(requestSpec.call()).thenReturn(callResponseSpec)
        whenever(callResponseSpec.content()).thenReturn(null)

        val result = aiService.prompt("Hello", emptyList())

        assertEquals("", result)
    }

    @Test
    fun `prompt propagates exceptions raised by the model call`() {
        whenever(chatClient.prompt()).thenReturn(requestSpec)
        whenever(requestSpec.messages(any<List<Message>>())).thenReturn(requestSpec)
        whenever(requestSpec.user(any<String>())).thenReturn(requestSpec)
        whenever(requestSpec.tools(aiTools)).thenReturn(requestSpec)
        whenever(requestSpec.call()).thenThrow(RuntimeException("Gemini call failed"))

        assertThrows(RuntimeException::class.java) { aiService.prompt("Hello", emptyList()) }
    }
}
