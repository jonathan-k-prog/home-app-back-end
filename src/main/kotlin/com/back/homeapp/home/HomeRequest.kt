package com.back.homeapp.home

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.Instant

data class HomeRequest(
    @field:NotBlank(message = "Home name is required")
    @field:Size(max = 100, message = "Home name must be 100 characters or less")
    val name: String,
    @field:NotBlank(message = "Home identifier is required")
    @field:Size(max = 100, message = "Home identifier must be 100 characters or less")
    val identifier: String,
    val timestamp: Instant,
)
