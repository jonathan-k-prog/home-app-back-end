package com.back.homeapp.message

import com.back.homeapp.conversation.Conversation
import com.back.homeapp.conversation.ConversationRepository
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
class MessageServiceTest {
    @Mock
    private lateinit var messageRepository: MessageRepository

    @Mock
    private lateinit var conversationRepository: ConversationRepository

    private lateinit var messageService: MessageService

    private val conversation = Conversation(id = 1, name = "Chat", timestamp = Instant.now())

    @BeforeEach
    fun setUp() {
        messageService = MessageService(messageRepository, conversationRepository)
    }

    private fun messageRequest(conversationId: Long = 1) =
        MessageRequest(id = null, text = "Hello", response = false, timestamp = 1000L, conversationId = conversationId)

    @Test
    fun `create saves a message when the conversation exists`() {
        whenever(conversationRepository.findById(1)).thenReturn(Optional.of(conversation))
        whenever(messageRepository.save(any())).thenAnswer { it.arguments[0] as Message }

        val result = messageService.create(messageRequest())

        assertEquals("Hello", result.text)
        assertEquals(false, result.response)
    }

    @Test
    fun `create throws when the conversation does not exist`() {
        whenever(conversationRepository.findById(99)).thenReturn(Optional.empty())

        assertThrows(IllegalArgumentException::class.java) {
            messageService.create(messageRequest(conversationId = 99))
        }
        verify(messageRepository, never()).save(any())
    }

    @Test
    fun `findAll with conversationId filters by conversation`() {
        val message = Message(id = 1, text = "Hi", timestamp = Instant.now(), conversation = conversation)
        whenever(messageRepository.findByConversationId(1)).thenReturn(listOf(message))

        val result = messageService.findAll(1)

        assertEquals(1, result.size)
        verify(messageRepository, never()).findAll()
    }

    @Test
    fun `findAll without conversationId returns everything`() {
        val message = Message(id = 1, text = "Hi", timestamp = Instant.now(), conversation = conversation)
        whenever(messageRepository.findAll()).thenReturn(listOf(message))

        val result = messageService.findAll(null)

        assertEquals(1, result.size)
    }

    @Test
    fun `findById returns the message when found`() {
        val message = Message(id = 1, text = "Hi", timestamp = Instant.now(), conversation = conversation)
        whenever(messageRepository.findById(1)).thenReturn(Optional.of(message))

        val result = messageService.findById(1)

        assertEquals(1L, result.id)
    }

    @Test
    fun `findById throws 404 when missing`() {
        whenever(messageRepository.findById(1)).thenReturn(Optional.empty())

        assertThrows(ResponseStatusException::class.java) { messageService.findById(1) }
    }

    @Test
    fun `update modifies and saves an existing message`() {
        val message = Message(id = 1, text = "Hi", timestamp = Instant.now(), conversation = conversation)
        whenever(messageRepository.findById(1)).thenReturn(Optional.of(message))
        whenever(conversationRepository.findById(1)).thenReturn(Optional.of(conversation))
        whenever(messageRepository.save(any())).thenAnswer { it.arguments[0] as Message }

        val result = messageService.update(1, messageRequest().copy(text = "Updated"))

        assertEquals("Updated", result.text)
    }

    @Test
    fun `update throws 404 when the message is missing`() {
        whenever(messageRepository.findById(1)).thenReturn(Optional.empty())

        assertThrows(ResponseStatusException::class.java) { messageService.update(1, messageRequest()) }
    }

    @Test
    fun `delete removes an existing message`() {
        val message = Message(id = 1, text = "Hi", timestamp = Instant.now(), conversation = conversation)
        whenever(messageRepository.findById(1)).thenReturn(Optional.of(message))

        val result = messageService.delete(1)

        assertEquals("Hi", result.text)
        verify(messageRepository).delete(message)
    }

    @Test
    fun `delete throws 404 when missing`() {
        whenever(messageRepository.findById(1)).thenReturn(Optional.empty())

        assertThrows(ResponseStatusException::class.java) { messageService.delete(1) }
        verify(messageRepository, never()).delete(any())
    }
}
