package com.back.homeapp.device

import com.back.homeapp.apiResponse.ApiResponse
import com.back.homeapp.room.RoomRequest
import com.back.homeapp.room.RoomResponse
import jakarta.validation.Valid
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
@RequestMapping("/api/devices")
class DeviceController(
    private val deviceService: DeviceService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @Valid @RequestBody request: DeviceRequest
    ): ResponseEntity<ApiResponse<DeviceResponse>> {
        println(request.type)

        val result = deviceService.create(request)

        val response = ApiResponse(
            status = "success",
            message = "Device created successfully",
            data = result,
            errors = null
        )

        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getAll(
    ): ResponseEntity<ApiResponse<List<DeviceResponse>>> {
        val result = deviceService.findAll()

        val response = ApiResponse(
            status = "success",
            message = "Devices fetched successfully",
            data = result,
            errors = null
        )

        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun getById(
        @Valid @PathVariable id: Long
    ): ResponseEntity<ApiResponse<DeviceResponse>> {
        val result = deviceService.findById(id)

        val response = ApiResponse(
            status = "success",
            message = "Device fetched successfully",
            data = result,
            errors = null
        )

        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    fun update(
        @Valid @PathVariable id: Long,
        @Valid @RequestBody request: DeviceRequest
    ): ResponseEntity<ApiResponse<DeviceResponse>> {
        val result = deviceService.update(id, request)

        val response = ApiResponse(
            status = "success",
            message = "Device updated successfully",
            data = result,
            errors = null
        )

        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Long
    ): ResponseEntity<ApiResponse<DeviceResponse>> {
        val result = deviceService.delete(id)

        val response = ApiResponse(
            status = "success",
            message = "Device deleted successfully",
            data = result,
            errors = null
        )

        return ResponseEntity.ok(response)
    }
}
