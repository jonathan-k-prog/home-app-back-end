package com.back.homeapp.device

import com.back.homeapp.room.RoomRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class DeviceService(
    private val deviceRepository: DeviceRepository,
    private val roomRepository: RoomRepository
) {
    fun create(request: DeviceRequest): DeviceResponse {
        val room = roomRepository.findById(request.roomId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Room ${request.roomId} not found") }

        val device = deviceRepository.save(
            Device(
                name = request.name,
                type = request.type,
                room = room
            )
        )

        return device.toResponse()
    }

    fun findAll(): List<DeviceResponse> =
        deviceRepository.findAll()
            .sortedBy { it.name.lowercase() }
            .map { it.toResponse() }

    fun findAllByRoomId(roomId: Long): List<DeviceResponse> =
        deviceRepository.findAllByRoomId(roomId)
            .sortedBy { it.name.lowercase() }
            .map { it.toResponse() }

    fun findById(id: Long): DeviceResponse =
        deviceRepository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Device $id not found") }
            .toResponse()

    fun update(id: Long, request: DeviceRequest): DeviceResponse {
        val device = deviceRepository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Device $id not found") }
        val room = roomRepository.findById(request.roomId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Room ${request.roomId} not found") }

        device.name = request.name
        device.type = request.type
        device.room = room

        return deviceRepository.save(device).toResponse()
    }

    fun delete(id: Long): DeviceResponse {
        val device = deviceRepository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Device $id not found") }

        val response = device.toResponse()
        deviceRepository.delete(device)

        return response
    }


}
