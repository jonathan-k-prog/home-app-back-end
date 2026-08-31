package com.back.homeapp.conversation

import com.back.homeapp.apiResponse.ApiResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
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
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/conversations")
@Validated
class ConversationController(
    private val conversationService: ConversationService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @Valid @RequestBody request: ConversationRequest,
    ): ResponseEntity<ApiResponse<ConversationResponse>> {
        val result = conversationService.create(request)

        val response =
            ApiResponse(
                status = "success",
                message = "Conversation created successfully",
                data = result,
                errors = null,
            )

        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getAll(): ResponseEntity<ApiResponse<List<ConversationResponse>>> {
        val result = conversationService.findAll()

        val response =
            ApiResponse(
                status = "success",
                message = "Conversations fetched successfully",
                data = result,
                errors = null,
            )

        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun getById(
        @Min(value = 1, message = "Id must be greater than 0") @PathVariable id: Long,
    ): ResponseEntity<ApiResponse<ConversationResponse>> {
        val result = conversationService.findById(id)

        val response =
            ApiResponse(
                status = "success",
                message = "Conversation fetched successfully",
                data = result,
                errors = null,
            )

        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    fun update(
        @Min(value = 1, message = "Id must be greater than 0") @PathVariable id: Long,
        @Valid @RequestBody request: ConversationRequest,
    ): ResponseEntity<ApiResponse<ConversationResponse>> {
        val result = conversationService.update(id, request)

        val response =
            ApiResponse(
                status = "success",
                message = "Conversation updated successfully",
                data = result,
                errors = null,
            )

        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun delete(
        @Min(value = 1, message = "Id must be greater than 0") @PathVariable id: Long,
    ): ResponseEntity<ApiResponse<ConversationResponse>> {
        val result = conversationService.delete(id)

        val response =
            ApiResponse(
                status = "success",
                message = "Conversation deleted successfully",
                data = result,
                errors = null,
            )

        return ResponseEntity.ok(response)
    }
}
