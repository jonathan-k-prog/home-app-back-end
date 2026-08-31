package com.back.homeapp.weather

import com.back.homeapp.apiResponse.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/weather")
class WeatherController(
    private val weatherService: WeatherService,
) {
    @GetMapping
    fun fetch(): ResponseEntity<ApiResponse<WeatherResponse>> {
        val result = weatherService.fetch()

        println(result)
        val response =
            ApiResponse(
                status = "success",
                message = "Weather fetched successfully",
                data = result,
                errors = null,
            )

        println(result)

        return ResponseEntity.ok(response)
    }
}
