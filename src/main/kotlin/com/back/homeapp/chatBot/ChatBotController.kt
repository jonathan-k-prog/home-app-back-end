package com.back.homeapp.chatBot

import com.back.homeapp.apiResponse.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/chat-bot")
class ChatBotController(
    private val chatBotService: ChatBotService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun talk(
        @RequestBody request: ChatBotRequest,
    ): ResponseEntity<ApiResponse<ChatBotResponse>> {
        val result = chatBotService.talk(request)

        println(result)

        val response =
            ApiResponse(
                status = "success",
                message = "Chat bot responded successfully",
                data = result,
                errors = null,
            )

        return ResponseEntity.ok(response)
    }

 /*   @GetMapping
    fun fetch(): ResponseEntity<ApiResponse<WeatherResponse>> {
        val result = weatherService.fetch()

        println(result)
        val response = ApiResponse(
            status = "success",
            message = "Weather fetched successfully",
            data = result,
            errors = null
        )

        println(result)

        return ResponseEntity.ok(response)
    }*/
}
