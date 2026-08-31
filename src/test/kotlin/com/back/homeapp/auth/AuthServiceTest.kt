package com.back.homeapp.auth

import com.back.homeapp.security.JwtService
import com.back.homeapp.user.User
import com.back.homeapp.user.UserRepository
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.lenient
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockConstruction
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@ExtendWith(MockitoExtension::class)
class AuthServiceTest {
    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var jwtService: JwtService

    private fun payloadFor(
        subject: String,
        email: String,
        emailVerified: Boolean = true,
        name: String? = "Plouf",
        picture: String? = "https://example.com/plouf.png",
    ): GoogleIdToken.Payload {
        val payload = mock(GoogleIdToken.Payload::class.java)
        lenient().`when`(payload.subject).thenReturn(subject)
        lenient().`when`(payload.email).thenReturn(email)
        lenient().`when`(payload.emailVerified).thenReturn(emailVerified)
        lenient().`when`(payload["name"]).thenReturn(name)
        lenient().`when`(payload["picture"]).thenReturn(picture)
        return payload
    }

    @Test
    fun `loginWithGoogle rejects an invalid token`() {
        mockConstruction(GoogleIdTokenVerifier::class.java) { verifierMock, _ ->
            whenever(verifierMock.verify(any<String>())).thenReturn(null)
        }.use {
            val authService = AuthService(userRepository, jwtService, "client-id", "plouf@example.com")

            val exception = assertThrows(ResponseStatusException::class.java) { authService.loginWithGoogle("bad-token") }

            assertEquals("401 UNAUTHORIZED \"Invalid Google token\"", exception.message)
        }
    }

    @Test
    fun `loginWithGoogle rejects a token for an unauthorized email`() {
        mockConstruction(GoogleIdTokenVerifier::class.java) { verifierMock, _ ->
            val idToken = mock(GoogleIdToken::class.java)
            val payload = payloadFor(subject = "google-1", email = "intruder@example.com")
            whenever(idToken.payload).thenReturn(payload)
            whenever(verifierMock.verify("token")).thenReturn(idToken)
        }.use {
            val authService = AuthService(userRepository, jwtService, "client-id", "plouf@example.com")

            val exception = assertThrows(ResponseStatusException::class.java) { authService.loginWithGoogle("token") }

            assertEquals(
                "403 FORBIDDEN \"This account is not authorized to access this application\"",
                exception.message,
            )
        }
    }

    @Test
    fun `loginWithGoogle rejects a token whose email is not verified`() {
        mockConstruction(GoogleIdTokenVerifier::class.java) { verifierMock, _ ->
            val idToken = mock(GoogleIdToken::class.java)
            val payload = payloadFor(subject = "google-1", email = "plouf@example.com", emailVerified = false)
            whenever(idToken.payload).thenReturn(payload)
            whenever(verifierMock.verify("token")).thenReturn(idToken)
        }.use {
            val authService = AuthService(userRepository, jwtService, "client-id", "plouf@example.com")

            assertThrows(ResponseStatusException::class.java) { authService.loginWithGoogle("token") }
        }
    }

    @Test
    fun `loginWithGoogle creates a new user on first login`() {
        mockConstruction(GoogleIdTokenVerifier::class.java) { verifierMock, _ ->
            val idToken = mock(GoogleIdToken::class.java)
            val payload = payloadFor(subject = "google-1", email = "plouf@example.com")
            whenever(idToken.payload).thenReturn(payload)
            whenever(verifierMock.verify("token")).thenReturn(idToken)
        }.use {
            whenever(userRepository.findByGoogleId("google-1")).thenReturn(null)
            whenever(userRepository.findByEmail("plouf@example.com")).thenReturn(null)
            whenever(userRepository.save(any())).thenAnswer { it.arguments[0] as User }
            whenever(jwtService.generateToken("plouf@example.com")).thenReturn("jwt-token")

            val authService = AuthService(userRepository, jwtService, "client-id", "plouf@example.com")
            val result = authService.loginWithGoogle("token")

            assertEquals("jwt-token", result.token)
            assertEquals("plouf@example.com", result.user.email)
        }
    }

    @Test
    fun `loginWithGoogle updates an existing user found by googleId`() {
        val existing =
            User(
                id = 1,
                email = "plouf@example.com",
                googleId = "google-1",
                name = "Old Name",
                timestamp = Instant.now(),
            )

        mockConstruction(GoogleIdTokenVerifier::class.java) { verifierMock, _ ->
            val idToken = mock(GoogleIdToken::class.java)
            val payload = payloadFor(subject = "google-1", email = "plouf@example.com", name = "New Name")
            whenever(idToken.payload).thenReturn(payload)
            whenever(verifierMock.verify("token")).thenReturn(idToken)
        }.use {
            whenever(userRepository.findByGoogleId("google-1")).thenReturn(existing)
            whenever(userRepository.save(any())).thenAnswer { it.arguments[0] as User }
            whenever(jwtService.generateToken("plouf@example.com")).thenReturn("jwt-token")

            val authService = AuthService(userRepository, jwtService, "client-id", "plouf@example.com")
            val result = authService.loginWithGoogle("token")

            assertEquals("New Name", result.user.name)
        }
    }

    @Test
    fun `getUserByEmail returns the user when found`() {
        mockConstruction(GoogleIdTokenVerifier::class.java).use {
            val user = User(id = 1, email = "plouf@example.com", googleId = "g-1", name = "Plouf", timestamp = Instant.now())
            whenever(userRepository.findByEmail("plouf@example.com")).thenReturn(user)

            val authService = AuthService(userRepository, jwtService, "client-id", "plouf@example.com")
            val result = authService.getUserByEmail("plouf@example.com")

            assertEquals("plouf@example.com", result.email)
        }
    }

    @Test
    fun `getUserByEmail throws 401 when the user is missing`() {
        mockConstruction(GoogleIdTokenVerifier::class.java).use {
            whenever(userRepository.findByEmail("missing@example.com")).thenReturn(null)

            val authService = AuthService(userRepository, jwtService, "client-id", "plouf@example.com")

            assertThrows(ResponseStatusException::class.java) { authService.getUserByEmail("missing@example.com") }
        }
    }
}
