package com.back.homeapp.room

import com.back.homeapp.home.Home
import com.back.homeapp.home.HomeRepository
import com.back.homeapp.roomType.RoomType
import com.back.homeapp.user.User
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.whenever
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class RoomServiceTest {
    @Mock
    private lateinit var roomRepository: RoomRepository

    @Mock
    private lateinit var homeRepository: HomeRepository

    private lateinit var roomService: RoomService

    private val user = User(id = 1, email = "plouf@example.com", googleId = "g-1", name = "Plouf", timestamp = Instant.now())
    private val home = Home(id = 1, name = "Maison", identifier = "home-1", timestamp = Instant.now(), creator = user)

    @BeforeEach
    fun setUp() {
        roomService = RoomService(roomRepository, homeRepository)
    }

    private fun roomRequest(homeId: Long = 1) =
        RoomRequest(
            name = "Salon",
            type = RoomType.DEFAULT,
            width = 10,
            height = 10,
            x = 0,
            y = 0,
            floor = 0,
            homeId = homeId,
        )

    @Test
    fun `create saves a room when home exists`() {
        whenever(homeRepository.findById(1)).thenReturn(Optional.of(home))
        whenever(roomRepository.save(any())).thenAnswer { it.arguments[0] as Room }

        val result = roomService.create(roomRequest())

        assertEquals("Salon", result.name)
        assertEquals(RoomType.DEFAULT, result.type)
        verify(roomRepository).save(any())
    }

    @Test
    fun `create throws 404 when home does not exist`() {
        whenever(homeRepository.findById(99)).thenReturn(Optional.empty())

        val exception =
            assertThrows(ResponseStatusException::class.java) {
                roomService.create(roomRequest(homeId = 99))
            }

        assertEquals("404 NOT_FOUND \"Home 99 not found\"", exception.message)
        verify(roomRepository, never()).save(any())
    }

    @Test
    fun `findAll without homeId returns everything sorted by name`() {
        val roomB = Room(id = 1, name = "Zebre", type = RoomType.DEFAULT, home = home)
        val roomA = Room(id = 2, name = "Alpha", type = RoomType.DEFAULT, home = home)
        whenever(roomRepository.findAll()).thenReturn(listOf(roomB, roomA))

        val result = roomService.findAll(null)

        assertEquals(listOf("Alpha", "Zebre"), result.map { it.name })
    }

    @Test
    fun `findAll with homeId filters by home`() {
        val room = Room(id = 1, name = "Salon", type = RoomType.DEFAULT, home = home)
        whenever(roomRepository.findAllByHomeId(1)).thenReturn(listOf(room))

        val result = roomService.findAll(1)

        assertEquals(1, result.size)
        verify(roomRepository, never()).findAll()
    }

    @Test
    fun `findById returns the room when found`() {
        val room = Room(id = 1, name = "Salon", type = RoomType.DEFAULT, home = home)
        whenever(roomRepository.findById(1)).thenReturn(Optional.of(room))

        val result = roomService.findById(1)

        assertEquals(1L, result.id)
    }

    @Test
    fun `findById throws 404 when room is missing`() {
        whenever(roomRepository.findById(1)).thenReturn(Optional.empty())

        val exception = assertThrows(ResponseStatusException::class.java) { roomService.findById(1) }

        assertEquals("404 NOT_FOUND \"Room 1 not found\"", exception.message)
    }

    @Test
    fun `update modifies and saves an existing room`() {
        val room = Room(id = 1, name = "Salon", type = RoomType.DEFAULT, home = home)
        whenever(roomRepository.findById(1)).thenReturn(Optional.of(room))
        whenever(homeRepository.findById(1)).thenReturn(Optional.of(home))
        whenever(roomRepository.save(any())).thenAnswer { it.arguments[0] as Room }

        val result = roomService.update(1, roomRequest().copy(name = "Salon renove"))

        assertEquals("Salon renove", result.name)
    }

    @Test
    fun `update throws 404 when room is missing`() {
        whenever(roomRepository.findById(1)).thenReturn(Optional.empty())

        assertThrows(ResponseStatusException::class.java) { roomService.update(1, roomRequest()) }
        verify(homeRepository, never()).findById(any())
    }

    @Test
    fun `update throws 404 when home is missing`() {
        val room = Room(id = 1, name = "Salon", type = RoomType.DEFAULT, home = home)
        whenever(roomRepository.findById(1)).thenReturn(Optional.of(room))
        whenever(homeRepository.findById(99)).thenReturn(Optional.empty())

        assertThrows(ResponseStatusException::class.java) { roomService.update(1, roomRequest(homeId = 99)) }
        verify(roomRepository, never()).save(any())
    }

    @Test
    fun `delete removes an existing room`() {
        val room = Room(id = 1, name = "Salon", type = RoomType.DEFAULT, home = home)
        whenever(roomRepository.findById(1)).thenReturn(Optional.of(room))

        val result = roomService.delete(1)

        assertEquals("Salon", result.name)
        verify(roomRepository).delete(room)
    }

    @Test
    fun `delete throws 404 when room is missing`() {
        whenever(roomRepository.findById(1)).thenReturn(Optional.empty())

        assertThrows(ResponseStatusException::class.java) { roomService.delete(1) }
        verify(roomRepository, never()).delete(any())
    }
}
