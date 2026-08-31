package com.back.homeapp.home

import com.back.homeapp.user.User
import com.back.homeapp.user.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class HomeServiceTest {
    @Mock
    private lateinit var homeRepository: HomeRepository

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var homeService: HomeService

    private val user = User(id = 1, email = "plouf@example.com", googleId = "g-1", name = "Plouf", timestamp = Instant.now())

    @BeforeEach
    fun setUp() {
        homeService = HomeService(homeRepository, userRepository)
    }

    private fun homeRequest() = HomeRequest(name = "Maison", identifier = "home-1", timestamp = Instant.now())

    @Test
    fun `create saves a home for the requesting user`() {
        whenever(userRepository.findByEmail("plouf@example.com")).thenReturn(user)
        whenever(homeRepository.save(any())).thenAnswer { it.arguments[0] as Home }

        val result = homeService.create(homeRequest(), "plouf@example.com")

        assertEquals("Maison", result.name)
        assertEquals("home-1", result.identifier)
    }

    @Test
    fun `create throws 404 when the user does not exist`() {
        whenever(userRepository.findByEmail("missing@example.com")).thenReturn(null)

        val exception =
            assertThrows(ResponseStatusException::class.java) {
                homeService.create(homeRequest(), "missing@example.com")
            }

        assertEquals("404 NOT_FOUND \"User missing@example.com not found\"", exception.message)
        verify(homeRepository, never()).save(any())
    }

    @Test
    fun `findAll returns homes sorted by name`() {
        val homeB = Home(id = 1, name = "Zebre", identifier = "z", timestamp = Instant.now(), creator = user)
        val homeA = Home(id = 2, name = "Alpha", identifier = "a", timestamp = Instant.now(), creator = user)
        whenever(homeRepository.findAll()).thenReturn(listOf(homeB, homeA))

        val result = homeService.findAll()

        assertEquals(listOf("Alpha", "Zebre"), result.map { it.name })
    }

    @Test
    fun `findById returns the home when found`() {
        val home = Home(id = 1, name = "Maison", identifier = "home-1", timestamp = Instant.now(), creator = user)
        whenever(homeRepository.findById(1)).thenReturn(Optional.of(home))

        val result = homeService.findById(1)

        assertEquals(1L, result.id)
    }

    @Test
    fun `findById throws when the home is missing`() {
        whenever(homeRepository.findById(1)).thenReturn(Optional.empty())

        assertThrows(Exception::class.java) { homeService.findById(1) }
    }

    @Test
    fun `update modifies and saves an existing home`() {
        val home = Home(id = 1, name = "Maison", identifier = "home-1", timestamp = Instant.now(), creator = user)
        whenever(homeRepository.findById(1)).thenReturn(Optional.of(home))
        whenever(homeRepository.save(any())).thenAnswer { it.arguments[0] as Home }

        val result = homeService.update(1, homeRequest().copy(name = "Maison renovee"))

        assertEquals("Maison renovee", result.name)
    }

    @Test
    fun `update throws 404 when the home is missing`() {
        whenever(homeRepository.findById(1)).thenReturn(Optional.empty())

        assertThrows(ResponseStatusException::class.java) { homeService.update(1, homeRequest()) }
    }

    @Test
    fun `delete removes an existing home`() {
        val home = Home(id = 1, name = "Maison", identifier = "home-1", timestamp = Instant.now(), creator = user)
        whenever(homeRepository.findById(1)).thenReturn(Optional.of(home))

        val result = homeService.delete(1)

        assertEquals("Maison", result.name)
        verify(homeRepository).delete(home)
    }

    @Test
    fun `delete throws 404 when the home is missing`() {
        whenever(homeRepository.findById(1)).thenReturn(Optional.empty())

        assertThrows(ResponseStatusException::class.java) { homeService.delete(1) }
        verify(homeRepository, never()).delete(any())
    }
}
