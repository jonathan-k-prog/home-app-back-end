package com.back.homeapp.conversation

import com.back.homeapp.apiResponse.ApiResponse
import com.back.homeapp.message.MessageRequest
import com.back.homeapp.message.MessageResponse
import com.back.homeapp.message.MessageService
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.data.repository.query.Param
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/messages")
@Validated
class MessageController(
    private val messageService: MessageService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @Valid @RequestBody request: MessageRequest,
    ): ResponseEntity<ApiResponse<MessageResponse>> {
        val result = messageService.create(request)

        val response =
            ApiResponse(
                status = "success",
                message = "Message created successfully",
                data = result,
                errors = null,
            )

        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getAll(
        @RequestParam conversationId: Long?,
    ): ResponseEntity<ApiResponse<List<MessageResponse>>> {
        val result = messageService.findAll(conversationId)

        val response =
            ApiResponse(
                status = "success",
                message = "Messages fetched successfully",
                data = result,
                errors = null,
            )

        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun getById(
        @Min(value = 1, message = "Id must be greater than 0") @PathVariable id: Long,
    ): ResponseEntity<ApiResponse<MessageResponse>> {
        val result = messageService.findById(id)

        val response =
            ApiResponse(
                status = "success",
                message = "Message fetched successfully",
                data = result,
                errors = null,
            )

        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    fun update(
        @Min(value = 1, message = "Id must be greater than 0") @PathVariable id: Long,
        @Valid @RequestBody request: MessageRequest,
    ): ResponseEntity<ApiResponse<MessageResponse>> {
        val result = messageService.update(id, request)

        val response =
            ApiResponse(
                status = "success",
                message = "Message updated successfully",
                data = result,
                errors = null,
            )

        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun delete(
        @Min(value = 1, message = "Id must be greater than 0") @PathVariable id: Long,
    ): ResponseEntity<ApiResponse<MessageResponse>> {
        val result = messageService.delete(id)

        val response =
            ApiResponse(
                status = "success",
                message = "Message deleted successfully",
                data = result,
                errors = null,
            )

        return ResponseEntity.ok(response)
    }
}
