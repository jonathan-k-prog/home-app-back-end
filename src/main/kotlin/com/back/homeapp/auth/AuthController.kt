package com.back.homeapp.auth

import com.back.homeapp.apiResponse.ApiResponse
import com.back.homeapp.user.UserResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping("/google")
    fun loginWithGoogle(
        @Valid @RequestBody request: GoogleAuthRequest,
    ): ResponseEntity<ApiResponse<AuthResponse>> {
        val result = authService.loginWithGoogle(request.idToken)

        val response =
            ApiResponse(
                status = "success",
                message = "Login successful",
                data = result,
                errors = null,
            )

        return ResponseEntity.ok(response)
    }

    @GetMapping("/me")
    fun me(): ResponseEntity<ApiResponse<UserResponse>> {
        val authentication = SecurityContextHolder.getContext().authentication
        val email = authentication?.principal as String
        val result = authService.getUserByEmail(email)

        val response =
            ApiResponse(
                status = "success",
                message = "User fetched successfully",
                data = result,
                errors = null,
            )

        return ResponseEntity.ok(response)
    }
}
