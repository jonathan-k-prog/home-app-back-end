package com.back.homeapp.room

import com.back.homeapp.home.HomeRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class RoomService(
    private val roomRepository: RoomRepository,
    private val homeRepository: HomeRepository
) {
    fun create(request: RoomRequest): RoomResponse {
        val home =
            homeRepository
                .findById(request.homeId)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Home ${request.homeId} not found") }

        val room =
            roomRepository.save(
                Room(
                    name = request.name,
                    type = request.type,
                    width = request.width,
                    height = request.height,
                    x = request.x,
                    y = request.y,
                    floor = request.floor,
                    home = home,
                ),
            )

        return room.toResponse()
    }

    fun findAll(homeId: Long?): List<RoomResponse> {
        val rooms =
            if (homeId != null) {
                roomRepository.findAllByHomeId(homeId)
            } else {
                roomRepository.findAll()
            }

        return rooms.sortedBy { it.name.lowercase() }.map { it.toResponse() }
    }

    fun findById(id: Long): RoomResponse =
        roomRepository
            .findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Room $id not found") }
            .toResponse()

    fun update(
        id: Long,
        request: RoomRequest,
    ): RoomResponse {
        val room =
            roomRepository
                .findById(id)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Room $id not found") }
        val home =
            homeRepository
                .findById(request.homeId)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Home $request.homeId not found") }

        room.name = request.name
        room.type = request.type
        room.x = request.x
        room.y = request.y
        room.width = request.width
        room.height = request.height
        room.floor = request.floor

        return roomRepository.save(room).toResponse()
    }

    fun delete(id: Long): RoomResponse {
        val room =
            roomRepository
                .findById(id)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Room $id not found") }

        val response = room.toResponse()
        roomRepository.delete(room)

        return response
    }
}
