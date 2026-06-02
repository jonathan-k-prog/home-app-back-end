package com.back.homeapp.room

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class RoomService(
    private val roomRepository: RoomRepository
) {
    fun create(request: RoomRequest): RoomResponse {
        val room = roomRepository.save(
            Room(
                name = request.name,
                type = request.type,
            )
        )

        return room.toResponse()
    }

    fun findAll(): List<RoomResponse> =
        roomRepository.findAll()
            .sortedBy { it.name.lowercase() }
            .map { it.toResponse() }

    fun findById(id: Long): RoomResponse =
        roomRepository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Room $id not found") }
            .toResponse()

    fun update(id: Long, request: RoomRequest): RoomResponse {
        val room = roomRepository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Room $id not found") }

        room.name = request.name
        room.type = request.type

        return roomRepository.save(room).toResponse()
    }

    fun delete(id: Long): RoomResponse {
        val room = roomRepository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Room $id not found") }

        val response = room.toResponse()
        roomRepository.delete(room)

        return response
    }
}
