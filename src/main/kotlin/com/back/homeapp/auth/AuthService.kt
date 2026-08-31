package com.back.homeapp.auth

import com.back.homeapp.security.JwtService
import com.back.homeapp.user.User
import com.back.homeapp.user.UserRepository
import com.back.homeapp.user.UserResponse
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtService: JwtService,
    @Value("\${google.client-id}") private val googleClientId: String,
    @Value("\${auth.allowed-email}") private val allowedEmail: String,
) {
    private val verifier =
        GoogleIdTokenVerifier
            .Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance())
            .setAudience(listOf(googleClientId))
            .build()

    fun loginWithGoogle(idTokenString: String): AuthResponse {
        val idToken =
            verifier.verify(idTokenString)
                ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Google token")

        val payload = idToken.payload

        if (payload.emailVerified != true || !payload.email.equals(allowedEmail, ignoreCase = true)) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "This account is not authorized to access this application")
        }

        val name = payload["name"] as? String ?: payload.email
        val picture = payload["picture"] as? String

        val user =
            (userRepository.findByGoogleId(payload.subject) ?: userRepository.findByEmail(payload.email))
                ?.apply {
                    googleId = payload.subject
                    this.name = name
                    pictureUrl = picture
                }
                ?: User(
                    email = payload.email,
                    googleId = payload.subject,
                    name = name,
                    pictureUrl = picture,
                )

        val savedUser = userRepository.save(user)
        val token = jwtService.generateToken(savedUser.email)

        return AuthResponse(token = token, user = savedUser.toResponse())
    }

    fun getUserByEmail(email: String): UserResponse {
        val user =
            userRepository.findByEmail(email)
                ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required")

        return user.toResponse()
    }
}
