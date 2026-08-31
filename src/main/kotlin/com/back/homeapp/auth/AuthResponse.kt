package com.back.homeapp.auth

import com.back.homeapp.user.UserResponse

data class AuthResponse(
    val token: String,
    val user: UserResponse,
)
