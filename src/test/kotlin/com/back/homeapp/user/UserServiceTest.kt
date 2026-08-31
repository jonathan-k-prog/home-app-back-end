package com.back.homeapp.user

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.time.Instant
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class UserServiceTest {
    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        userService = UserService(userRepository)
    }

    @Test
    fun `findById returns the user when found`() {
        val user = User(id = 1, email = "plouf@example.com", googleId = "g-1", name = "Plouf", timestamp = Instant.now())
        whenever(userRepository.findById(1)).thenReturn(Optional.of(user))

        val result = userService.findById(1)

        assertEquals("plouf@example.com", result.email)
    }

    @Test
    fun `findById throws when the user is missing`() {
        whenever(userRepository.findById(1)).thenReturn(Optional.empty())

        assertThrows(Exception::class.java) { userService.findById(1) }
    }
}
