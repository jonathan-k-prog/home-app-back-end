package com.back.homeapp.room

import com.back.homeapp.apiResponse.ApiResponse
import com.back.homeapp.device.DeviceResponse
import com.back.homeapp.device.DeviceService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/rooms")
class RoomController(
    private val roomService: RoomService,
    private val deviceService: DeviceService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: RoomRequest): ResponseEntity<ApiResponse<RoomResponse>> {
        println(request)
        val result = roomService.create(request)

        val response = ApiResponse(
            status = "success",
            message = "Room created successfully",
            data = result,
            errors = null
        )

        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getAll(): ResponseEntity<ApiResponse<List<RoomResponse>>> {
        val result = roomService.findAll()

        val response = ApiResponse(
            status = "success",
            message = "Rooms fetched successfully",
            data = result,
            errors = null
        )

        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}/devices")
    fun getDevicesByRoomId(
        @PathVariable id: Long
    ): ResponseEntity<ApiResponse<List<DeviceResponse>>> {
        val result = deviceService.findAllByRoomId(id)

        val response = ApiResponse(
            status = "success",
            message = "Devices fetched successfully",
            data = result,
            errors = null
        )

        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): RoomResponse = roomService.findById(id)

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody request: RoomRequest
    ): ResponseEntity<ApiResponse<RoomResponse>> {
        val result = roomService.update(id, request)

        val response = ApiResponse(
            status = "success",
            message = "Room updated successfully",
            data = result,
            errors = null
        )

        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<ApiResponse<RoomResponse>> {
        val result = roomService.delete(id)

        val response = ApiResponse(
            status = "success",
            message = "Room deleted successfully",
            data = result,
            errors = null
        )

        return ResponseEntity.ok(response)
    }
}
