package com.back.homeapp.conversation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class ConversationServiceTest {
    @Mock
    private lateinit var conversationRepository: ConversationRepository

    private lateinit var conversationService: ConversationService

    @BeforeEach
    fun setUp() {
        conversationService = ConversationService(conversationRepository)
    }

    @Test
    fun `create saves a new conversation`() {
        whenever(conversationRepository.save(any())).thenAnswer { it.arguments[0] as Conversation }

        val result = conversationService.create(ConversationRequest(id = null, name = "New chat", timestamp = 1000L))

        assertEquals("New chat", result.name)
        assertEquals(1000L, result.timestamp)
    }

    @Test
    fun `findAll returns conversations sorted by timestamp`() {
        val newer = Conversation(id = 1, name = "Newer", timestamp = Instant.ofEpochMilli(2000))
        val older = Conversation(id = 2, name = "Older", timestamp = Instant.ofEpochMilli(1000))
        whenever(conversationRepository.findAll()).thenReturn(listOf(newer, older))

        val result = conversationService.findAll()

        assertEquals(listOf("Older", "Newer"), result.map { it.name })
    }

    @Test
    fun `findById returns the conversation when found`() {
        val conversation = Conversation(id = 1, name = "Chat", timestamp = Instant.now())
        whenever(conversationRepository.findById(1)).thenReturn(Optional.of(conversation))

        val result = conversationService.findById(1)

        assertEquals(1L, result.id)
    }

    @Test
    fun `findById throws 404 when missing`() {
        whenever(conversationRepository.findById(1)).thenReturn(Optional.empty())

        assertThrows(ResponseStatusException::class.java) { conversationService.findById(1) }
    }

    @Test
    fun `update modifies and saves an existing conversation`() {
        val conversation = Conversation(id = 1, name = "Chat", timestamp = Instant.ofEpochMilli(1000))
        whenever(conversationRepository.findById(1)).thenReturn(Optional.of(conversation))
        whenever(conversationRepository.save(any())).thenAnswer { it.arguments[0] as Conversation }

        val result = conversationService.update(1, ConversationRequest(id = 1, name = "Renamed", timestamp = 2000L))

        assertEquals("Renamed", result.name)
        assertEquals(2000L, result.timestamp)
    }

    @Test
    fun `update throws 404 when missing`() {
        whenever(conversationRepository.findById(1)).thenReturn(Optional.empty())

        assertThrows(ResponseStatusException::class.java) {
            conversationService.update(1, ConversationRequest(id = 1, name = "Renamed", timestamp = 2000L))
        }
        verify(conversationRepository, never()).save(any())
    }

    @Test
    fun `delete removes an existing conversation`() {
        val conversation = Conversation(id = 1, name = "Chat", timestamp = Instant.now())
        whenever(conversationRepository.findById(1)).thenReturn(Optional.of(conversation))

        val result = conversationService.delete(1)

        assertEquals("Chat", result.name)
        verify(conversationRepository).delete(conversation)
    }

    @Test
    fun `delete throws 404 when missing`() {
        whenever(conversationRepository.findById(1)).thenReturn(Optional.empty())

        assertThrows(ResponseStatusException::class.java) { conversationService.delete(1) }
        verify(conversationRepository, never()).delete(any())
    }
}
