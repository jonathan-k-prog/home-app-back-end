package com.back.homeapp.command

import com.back.homeapp.device.DeviceRepository
import com.back.homeapp.mqtt.MqttPublisherService
import com.back.homeapp.room.RoomRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class CommandService(
    private val mqttPublisherService: MqttPublisherService?,
    private val deviceRepository: DeviceRepository,
    private val roomRepository: RoomRepository,
) {
    fun send(request: CommandRequest): String {
        when (request.targetType) {
            CommandTargetType.DEVICE -> {
                deviceRepository
                    .findById(request.targetId)
                    .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Device ${request.targetId} not found") }
                mqttPublisherService?.publish("esp32/dht11/command/${request.targetId}", request.action)
            }
            CommandTargetType.ROOM -> {
                roomRepository
                    .findById(request.targetId)
                    .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Room ${request.targetId} not found") }
                mqttPublisherService?.publish("esp32/commands/room/${request.targetId}", request.action)
            }
        }

        return "Command '${request.action}' sent to ${request.targetType.name.lowercase()} ${request.targetId}"
    }
}
