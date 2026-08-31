package com.back.homeapp.device

import com.back.homeapp.room.RoomRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@Service
class DeviceService(
    private val deviceRepository: DeviceRepository,
    private val roomRepository: RoomRepository,
) {
    fun create(request: DeviceRequest): DeviceResponse {
        val room =
            roomRepository
                .findById(request.roomId)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Room ${request.roomId} not found") }

        val device =
            deviceRepository.save(
                Device(
                    name = request.name,
                    identifier = request.identifier,
                    type = request.type,
                    connected = false,
                    lastSeen = Instant.now(),
                    room = room,
                ),
            )

        return device.toResponse()
    }

    fun findAll(
        connected: Boolean? = null,
        roomId: Long? = null,
        homeId: Long? = null,
    ): List<DeviceResponse> {
        val devices =
            if (connected != null) {
                deviceRepository.findAllByConnected(connected)
            } else if (roomId != null) {
                deviceRepository.findAllByRoomId(roomId)
            } else if (homeId != null) {
                deviceRepository.findAllByRoom_Home_Id(homeId)
            } else {
                deviceRepository.findAll()
            }
        return devices.sortedBy { it.name.lowercase() }.map { it.toResponse() }
    }

    fun findById(id: Long): DeviceResponse =
        deviceRepository
            .findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Device $id not found") }
            .toResponse()

    @Transactional(readOnly = true)
    fun findByIdentifierAndHomeIdentifier(
        identifier: String,
        homeIdentifier: String,
    ): DeviceResponse {
        val device =
            deviceRepository
                .findByIdentifierAndRoom_Home_Identifier(identifier, homeIdentifier)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Device $identifier for home $homeIdentifier not found")

        return device.toResponse()
    }

    fun update(
        id: Long,
        request: DeviceRequest,
    ): DeviceResponse {
        val device =
            deviceRepository
                .findById(id)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Device $id not found") }
        val room =
            roomRepository
                .findById(request.roomId)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Room ${request.roomId} not found") }

        device.name = request.name
        device.type = request.type
        device.identifier = request.identifier
        device.connected = request.connected
        device.lastSeen = request.lastSeen
        device.room = room

        return deviceRepository.save(device).toResponse()
    }

    @Transactional
    fun markConnected(
        id: Long,
        at: Instant = Instant.now(),
    ): DeviceResponse {
        val device =
            deviceRepository
                .findById(id)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Device $id not found") }

        device.markConnected(at)

        return device.toResponse()
    }

    fun delete(id: Long): DeviceResponse {
        val device =
            deviceRepository
                .findById(id)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Device $id not found") }

        val response = device.toResponse()
        deviceRepository.delete(device)

        return response
    }
}
