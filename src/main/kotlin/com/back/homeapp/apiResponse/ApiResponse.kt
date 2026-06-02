package com.back.homeapp.apiResponse

data class ApiResponse<T>(
    val status: String,
    val message: String,
    val data: T?,
    val errors: Any?
)
