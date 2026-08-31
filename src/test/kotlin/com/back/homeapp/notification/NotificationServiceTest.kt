package com.back.homeapp.notification

import com.back.homeapp.mqtt.MqttPublisherService
import com.back.homeapp.notificationType.NotificationType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.web.server.ResponseStatusException
import tools.jackson.databind.json.JsonMapper
import java.time.Instant
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class NotificationServiceTest {
    @Mock
    private lateinit var mqttPublisherService: MqttPublisherService

    @Mock
    private lateinit var notificationRepository: NotificationRepository

    private val objectMapper = JsonMapper.builder().build()

    private fun request() =
        NotificationRequest(type = NotificationType.INFO, message = "Hello", timestamp = Instant.now(), read = false)

    @Test
    fun `publish serializes and sends the notification over mqtt`() {
        val notificationService = NotificationService(mqttPublisherService, notificationRepository, objectMapper)
        val notification = NotificationResponse(id = 1, type = NotificationType.INFO, message = "Hello", timestamp = Instant.now(), read = false)

        notificationService.publish(notification)

        verify(mqttPublisherService).publish(eq("home-app-back-end/notification"), any(), any())
    }

    @Test
    fun `publish works without a configured mqtt publisher`() {
        val notificationService = NotificationService(null, notificationRepository, objectMapper)
        val notification = NotificationResponse(id = 1, type = NotificationType.INFO, message = "Hello", timestamp = Instant.now(), read = false)

        notificationService.publish(notification)
    }

    @Test
    fun `create saves a notification`() {
        val notificationService = NotificationService(mqttPublisherService, notificationRepository, objectMapper)
        whenever(notificationRepository.save(any())).thenAnswer { it.arguments[0] as Notification }

        val result = notificationService.create(request())

        assertEquals("Hello", result.message)
        assertEquals(NotificationType.INFO, result.type)
    }

    @Test
    fun `findAll returns notifications sorted by timestamp`() {
        val notificationService = NotificationService(mqttPublisherService, notificationRepository, objectMapper)
        val newer = Notification(id = 1, type = NotificationType.INFO, message = "Newer", timestamp = Instant.ofEpochMilli(2000))
        val older = Notification(id = 2, type = NotificationType.WARN, message = "Older", timestamp = Instant.ofEpochMilli(1000))
        whenever(notificationRepository.findAll()).thenReturn(listOf(newer, older))

        val result = notificationService.findAll()

        assertEquals(listOf("Older", "Newer"), result.map { it.message })
    }

    @Test
    fun `findById returns the notification when found`() {
        val notificationService = NotificationService(mqttPublisherService, notificationRepository, objectMapper)
        val notification = Notification(id = 1, type = NotificationType.INFO, message = "Hello", timestamp = Instant.now())
        whenever(notificationRepository.findById(1)).thenReturn(Optional.of(notification))

        val result = notificationService.findById(1)

        assertEquals(1L, result.id)
    }

    @Test
    fun `findById throws 404 when missing`() {
        val notificationService = NotificationService(mqttPublisherService, notificationRepository, objectMapper)
        whenever(notificationRepository.findById(1)).thenReturn(Optional.empty())

        assertThrows(ResponseStatusException::class.java) { notificationService.findById(1) }
    }

    @Test
    fun `update modifies and saves an existing notification`() {
        val notificationService = NotificationService(mqttPublisherService, notificationRepository, objectMapper)
        val notification = Notification(id = 1, type = NotificationType.INFO, message = "Hello", timestamp = Instant.now())
        whenever(notificationRepository.findById(1)).thenReturn(Optional.of(notification))
        whenever(notificationRepository.save(any())).thenAnswer { it.arguments[0] as Notification }

        val result = notificationService.update(1, request().copy(message = "Updated", read = true))

        assertEquals("Updated", result.message)
        assertEquals(true, result.read)
    }

    @Test
    fun `update throws 404 when missing`() {
        val notificationService = NotificationService(mqttPublisherService, notificationRepository, objectMapper)
        whenever(notificationRepository.findById(1)).thenReturn(Optional.empty())

        assertThrows(ResponseStatusException::class.java) { notificationService.update(1, request()) }
    }

    @Test
    fun `delete removes an existing notification`() {
        val notificationService = NotificationService(mqttPublisherService, notificationRepository, objectMapper)
        val notification = Notification(id = 1, type = NotificationType.INFO, message = "Hello", timestamp = Instant.now())
        whenever(notificationRepository.findById(1)).thenReturn(Optional.of(notification))

        val result = notificationService.delete(1)

        assertEquals("Hello", result.message)
        verify(notificationRepository).delete(notification)
    }

    @Test
    fun `delete throws 404 when missing`() {
        val notificationService = NotificationService(mqttPublisherService, notificationRepository, objectMapper)
        whenever(notificationRepository.findById(1)).thenReturn(Optional.empty())

        assertThrows(ResponseStatusException::class.java) { notificationService.delete(1) }
        verify(notificationRepository, never()).delete(any())
    }
}
