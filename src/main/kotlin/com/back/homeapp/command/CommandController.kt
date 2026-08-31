package com.back.homeapp.command

import com.back.homeapp.apiResponse.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/commands")
class CommandController(
    private val commandService: CommandService,
) {
    @PostMapping
    fun send(
        @Valid @RequestBody request: CommandRequest,
    ): ResponseEntity<ApiResponse<Nothing>> {
        val message = commandService.send(request)

        val response =
            ApiResponse(
                status = "success",
                message = message,
                data = null,
                errors = null,
            )

        return ResponseEntity.ok(response)
    }
}
