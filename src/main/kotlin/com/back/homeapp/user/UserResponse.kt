package com.back.homeapp.user

data class UserResponse(
    val id: Long?,
    val email: String,
    val name: String,
    val pictureUrl: String?,
)
