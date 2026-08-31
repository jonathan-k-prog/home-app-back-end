package com.back.homeapp.notification

import com.back.homeapp.mqtt.MqttPublisherService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import tools.jackson.databind.ObjectMapper

@Service
class NotificationService(
    private val mqttPublisherService: MqttPublisherService?,
    private val notificationRepository: NotificationRepository,
    private val objectMapper: ObjectMapper,
) {
    fun publish(notification: NotificationResponse) {
        val payload = objectMapper.writeValueAsString(notification)

        mqttPublisherService?.publish("home-app-back-end/notification", payload)
    }

    fun create(request: NotificationRequest): NotificationResponse {
        val notification =
            notificationRepository.save(
                Notification(
                    type = request.type,
                    message = request.message,
                    timestamp = request.timestamp,
                    read = request.read,
                ),
            )

        return notification.toResponse()
    }

    fun findAll(): List<NotificationResponse> =
        notificationRepository
            .findAll()
            .sortedBy { it.timestamp }
            .map { it.toResponse() }

    fun findById(id: Long): NotificationResponse =
        notificationRepository
            .findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Notification $id not found") }
            .toResponse()

    fun update(
        id: Long,
        request: NotificationRequest,
    ): NotificationResponse {
        val notification =
            notificationRepository
                .findById(id)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Notification $id not found") }

        notification.type = request.type
        notification.message = request.message
        notification.timestamp = request.timestamp
        notification.read = request.read

        return notificationRepository.save(notification).toResponse()
    }

    fun delete(id: Long): NotificationResponse {
        val notification =
            notificationRepository
                .findById(id)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Notification $id not found") }

        val response = notification.toResponse()
        notificationRepository.delete(notification)

        return response
    }
}
