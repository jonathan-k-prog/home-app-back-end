package com.back.homeapp.security

import io.jsonwebtoken.ExpiredJwtException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class JwtServiceTest {
    private val secret = "a-very-long-test-secret-used-only-for-unit-tests-1234567890"

    @Test
    fun `generateToken produces a token that extractEmail can decode`() {
        val jwtService = JwtService(secret, 60_000)

        val token = jwtService.generateToken("plouf@example.com")

        assertEquals("plouf@example.com", jwtService.extractEmail(token))
    }

    @Test
    fun `isValid returns true for a freshly generated token`() {
        val jwtService = JwtService(secret, 60_000)

        val token = jwtService.generateToken("plouf@example.com")

        assertTrue(jwtService.isValid(token))
    }

    @Test
    fun `isValid returns false for a garbage token`() {
        val jwtService = JwtService(secret, 60_000)

        assertFalse(jwtService.isValid("not-a-real-token"))
    }

    @Test
    fun `isValid returns false for an expired token`() {
        val jwtService = JwtService(secret, -1)

        val token = jwtService.generateToken("plouf@example.com")

        assertFalse(jwtService.isValid(token))
    }

    @Test
    fun `extractEmail throws for an expired token`() {
        val jwtService = JwtService(secret, -1)

        val token = jwtService.generateToken("plouf@example.com")

        assertThrows(ExpiredJwtException::class.java) { jwtService.extractEmail(token) }
    }

    @Test
    fun `isValid returns false when the token was signed with a different secret`() {
        val jwtService = JwtService(secret, 60_000)
        val otherService = JwtService("a-completely-different-test-secret-1234567890abcdef", 60_000)

        val token = otherService.generateToken("plouf@example.com")

        assertFalse(jwtService.isValid(token))
    }

    @Test
    fun `tokens for different subjects are not equal`() {
        val jwtService = JwtService(secret, 60_000)

        val tokenOne = jwtService.generateToken("one@example.com")
        val tokenTwo = jwtService.generateToken("two@example.com")

        assertNotEquals(tokenOne, tokenTwo)
    }
}
