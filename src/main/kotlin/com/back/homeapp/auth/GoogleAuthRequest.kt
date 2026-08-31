package com.back.homeapp.auth

import jakarta.validation.constraints.NotBlank

data class GoogleAuthRequest(
    @field:NotBlank
    val idToken: String,
)
