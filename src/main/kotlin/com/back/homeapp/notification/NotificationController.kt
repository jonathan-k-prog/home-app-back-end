package com.back.homeapp.notification

import com.back.homeapp.apiResponse.ApiResponse
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
@RequestMapping("/api/notifications")
class NotificationController(
    private val notificationService: NotificationService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @Valid @RequestBody request: NotificationRequest,
    ): ResponseEntity<ApiResponse<NotificationResponse>> {
        val result = notificationService.create(request)

        val response =
            ApiResponse(
                status = "success",
                message = "Notification created successfully",
                data = result,
                errors = null,
            )

        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getAll(): ResponseEntity<ApiResponse<List<NotificationResponse>>> {
        val result = notificationService.findAll()

        val response =
            ApiResponse(
                status = "success",
                message = "Notifications fetched successfully",
                data = result,
                errors = null,
            )

        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun getById(
        @Valid @PathVariable id: Long,
    ): ResponseEntity<ApiResponse<NotificationResponse>> {
        val result = notificationService.findById(id)

        val response =
            ApiResponse(
                status = "success",
                message = "Notification fetched successfully",
                data = result,
                errors = null,
            )

        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    fun update(
        @Valid @PathVariable id: Long,
        @Valid @RequestBody request: NotificationRequest,
    ): ResponseEntity<ApiResponse<NotificationResponse>> {
        val result = notificationService.update(id, request)

        val response =
            ApiResponse(
                status = "success",
                message = "Notification updated successfully",
                data = result,
                errors = null,
            )

        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Long,
    ): ResponseEntity<ApiResponse<NotificationResponse>> {
        val result = notificationService.delete(id)

        val response =
            ApiResponse(
                status = "success",
                message = "Notification deleted successfully",
                data = result,
                errors = null,
            )

        return ResponseEntity.ok(response)
    }
}
