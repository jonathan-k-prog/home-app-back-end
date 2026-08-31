package com.back.homeapp.chatBot

import com.back.homeapp.ai.AiService
import com.back.homeapp.conversation.ConversationRequest
import com.back.homeapp.conversation.ConversationResponse
import com.back.homeapp.conversation.ConversationService
import com.back.homeapp.message.MessageRequest
import com.back.homeapp.message.MessageResponse
import com.back.homeapp.message.MessageService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class ChatBotServiceTest {
    @Mock
    private lateinit var aiService: AiService

    @Mock
    private lateinit var conversationService: ConversationService

    @Mock
    private lateinit var messageService: MessageService

    @InjectMocks
    private lateinit var chatBotService: ChatBotService

    private val conversation = ConversationResponse(id = 1, name = "New Conversation", timestamp = 1000L)

    private fun message(
        text: String,
        response: Boolean,
    ) = MessageResponse(id = null, text = text, response = response, timestamp = 1000L, conversation = conversation)

    @Test
    fun `talk creates a new conversation when none is given`() {
        whenever(conversationService.create(any())).thenReturn(conversation)
        whenever(messageService.findAll(1)).thenReturn(emptyList())
        whenever(messageService.create(any())).thenAnswer {
            val request = it.arguments[0] as MessageRequest
            message(request.text, request.response)
        }
        whenever(aiService.prompt(eq("Hello"), any())).thenReturn("Hi there")

        val result = chatBotService.talk(ChatBotRequest(text = "Hello", timestamp = 1000L, conversationId = null))

        assertEquals("Hello", result.question.text)
        assertEquals("Hi there", result.answer.text)
        verify(conversationService).create(any())
        verify(conversationService, never()).findById(any())
    }

    @Test
    fun `talk reuses an existing conversation when an id is given`() {
        whenever(conversationService.findById(1)).thenReturn(conversation)
        whenever(messageService.findAll(1)).thenReturn(emptyList())
        whenever(messageService.create(any())).thenAnswer {
            val request = it.arguments[0] as MessageRequest
            message(request.text, request.response)
        }
        whenever(aiService.prompt(eq("Hello"), any())).thenReturn("Hi there")

        val result = chatBotService.talk(ChatBotRequest(text = "Hello", timestamp = 1000L, conversationId = 1))

        assertEquals(1L, result.conversation.id)
        verify(conversationService, never()).create(any())
    }

    @Test
    fun `talk propagates exceptions raised while generating the reply`() {
        whenever(conversationService.findById(1)).thenReturn(conversation)
        whenever(messageService.findAll(1)).thenReturn(emptyList())
        whenever(messageService.create(any())).thenAnswer {
            val request = it.arguments[0] as MessageRequest
            message(request.text, request.response)
        }
        whenever(aiService.prompt(any(), any())).thenThrow(RuntimeException("Gemini down"))

        assertThrows(RuntimeException::class.java) {
            chatBotService.talk(ChatBotRequest(text = "Hello", timestamp = 1000L, conversationId = 1))
        }
    }
}
