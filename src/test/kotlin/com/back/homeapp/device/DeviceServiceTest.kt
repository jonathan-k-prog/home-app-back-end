package com.back.homeapp.device

import com.back.homeapp.deviceType.DeviceType
import com.back.homeapp.home.Home
import com.back.homeapp.room.Room
import com.back.homeapp.room.RoomRepository
import com.back.homeapp.roomType.RoomType
import com.back.homeapp.user.User
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
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
class DeviceServiceTest {
    @Mock
    private lateinit var deviceRepository: DeviceRepository

    @Mock
    private lateinit var roomRepository: RoomRepository

    private lateinit var deviceService: DeviceService

    private val user = User(id = 1, email = "plouf@example.com", googleId = "g-1", name = "Plouf", timestamp = Instant.now())
    private val home = Home(id = 1, name = "Maison", identifier = "home-1", timestamp = Instant.now(), creator = user)
    private val room = Room(id = 1, name = "Salon", type = RoomType.DEFAULT, home = home)

    @BeforeEach
    fun setUp() {
        deviceService = DeviceService(deviceRepository, roomRepository)
    }

    private fun deviceRequest(roomId: Long = 1) =
        DeviceRequest(
            name = "DHT11",
            identifier = "esp32-1",
            connected = false,
            lastSeen = Instant.now(),
            type = DeviceType.ESP_32_DHT11,
            roomId = roomId,
        )

    @Test
    fun `create saves a device when room exists`() {
        whenever(roomRepository.findById(1)).thenReturn(Optional.of(room))
        whenever(deviceRepository.save(any())).thenAnswer { it.arguments[0] as Device }

        val result = deviceService.create(deviceRequest())

        assertEquals("DHT11", result.name)
        assertEquals(false, result.connected)
    }

    @Test
    fun `create throws 404 when room does not exist`() {
        whenever(roomRepository.findById(99)).thenReturn(Optional.empty())

        val exception =
            assertThrows(ResponseStatusException::class.java) {
                deviceService.create(deviceRequest(roomId = 99))
            }

        assertEquals("404 NOT_FOUND \"Room 99 not found\"", exception.message)
        verify(deviceRepository, never()).save(any())
    }

    @Test
    fun `findAll with connected filter delegates to repository`() {
        val device = Device(id = 1, name = "DHT11", identifier = "esp32-1", room = room)
        whenever(deviceRepository.findAllByConnected(true)).thenReturn(listOf(device))

        val result = deviceService.findAll(connected = true)

        assertEquals(1, result.size)
        verify(deviceRepository, never()).findAll()
    }

    @Test
    fun `findAll with roomId filter delegates to repository`() {
        val device = Device(id = 1, name = "DHT11", identifier = "esp32-1", room = room)
        whenever(deviceRepository.findAllByRoomId(1)).thenReturn(listOf(device))

        val result = deviceService.findAll(roomId = 1)

        assertEquals(1, result.size)
    }

    @Test
    fun `findAll with homeId filter delegates to repository`() {
        val device = Device(id = 1, name = "DHT11", identifier = "esp32-1", room = room)
        whenever(deviceRepository.findAllByRoom_Home_Id(1)).thenReturn(listOf(device))

        val result = deviceService.findAll(homeId = 1)

        assertEquals(1, result.size)
    }

    @Test
    fun `findAll without filters returns all sorted by name`() {
        val deviceB = Device(id = 1, name = "Zebre", identifier = "esp32-1", room = room)
        val deviceA = Device(id = 2, name = "Alpha", identifier = "esp32-2", room = room)
        whenever(deviceRepository.findAll()).thenReturn(listOf(deviceB, deviceA))

        val result = deviceService.findAll()

        assertEquals(listOf("Alpha", "Zebre"), result.map { it.name })
    }

    @Test
    fun `findById returns the device when found`() {
        val device = Device(id = 1, name = "DHT11", identifier = "esp32-1", room = room)
        whenever(deviceRepository.findById(1)).thenReturn(Optional.of(device))

        val result = deviceService.findById(1)

        assertEquals(1L, result.id)
    }

    @Test
    fun `findById throws 404 when device is missing`() {
        whenever(deviceRepository.findById(1)).thenReturn(Optional.empty())

        assertThrows(ResponseStatusException::class.java) { deviceService.findById(1) }
    }

    @Test
    fun `findByIdentifierAndHomeIdentifier returns the device when found`() {
        val device = Device(id = 1, name = "DHT11", identifier = "esp32-1", room = room)
        whenever(deviceRepository.findByIdentifierAndRoom_Home_Identifier("esp32-1", "home-1")).thenReturn(device)

        val result = deviceService.findByIdentifierAndHomeIdentifier("esp32-1", "home-1")

        assertEquals("esp32-1", result.identifier)
    }

    @Test
    fun `findByIdentifierAndHomeIdentifier throws 404 when device is missing`() {
        whenever(deviceRepository.findByIdentifierAndRoom_Home_Identifier("missing", "home-1")).thenReturn(null)

        assertThrows(ResponseStatusException::class.java) {
            deviceService.findByIdentifierAndHomeIdentifier("missing", "home-1")
        }
    }

    @Test
    fun `update modifies and saves an existing device`() {
        val device = Device(id = 1, name = "DHT11", identifier = "esp32-1", room = room)
        whenever(deviceRepository.findById(1)).thenReturn(Optional.of(device))
        whenever(roomRepository.findById(1)).thenReturn(Optional.of(room))
        whenever(deviceRepository.save(any())).thenAnswer { it.arguments[0] as Device }

        val result = deviceService.update(1, deviceRequest().copy(name = "DHT22"))

        assertEquals("DHT22", result.name)
    }

    @Test
    fun `update throws 404 when device is missing`() {
        whenever(deviceRepository.findById(1)).thenReturn(Optional.empty())

        assertThrows(ResponseStatusException::class.java) { deviceService.update(1, deviceRequest()) }
        verify(roomRepository, never()).findById(any())
    }

    @Test
    fun `update throws 404 when room is missing`() {
        val device = Device(id = 1, name = "DHT11", identifier = "esp32-1", room = room)
        whenever(deviceRepository.findById(1)).thenReturn(Optional.of(device))
        whenever(roomRepository.findById(99)).thenReturn(Optional.empty())

        assertThrows(ResponseStatusException::class.java) { deviceService.update(1, deviceRequest(roomId = 99)) }
        verify(deviceRepository, never()).save(any())
    }

    @Test
    fun `markConnected marks the device connected at the given instant`() {
        val device = Device(id = 1, name = "DHT11", identifier = "esp32-1", connected = false, room = room)
        whenever(deviceRepository.findById(1)).thenReturn(Optional.of(device))
        val now = Instant.now()

        val result = deviceService.markConnected(1, now)

        assertTrue(result.connected)
        assertEquals(now, result.lastSeen)
    }

    @Test
    fun `markConnected throws 404 when device is missing`() {
        whenever(deviceRepository.findById(1)).thenReturn(Optional.empty())

        assertThrows(ResponseStatusException::class.java) { deviceService.markConnected(1) }
    }

    @Test
    fun `delete removes an existing device`() {
        val device = Device(id = 1, name = "DHT11", identifier = "esp32-1", room = room)
        whenever(deviceRepository.findById(1)).thenReturn(Optional.of(device))

        val result = deviceService.delete(1)

        assertEquals("DHT11", result.name)
        verify(deviceRepository).delete(device)
    }

    @Test
    fun `delete throws 404 when device is missing`() {
        whenever(deviceRepository.findById(1)).thenReturn(Optional.empty())

        assertThrows(ResponseStatusException::class.java) { deviceService.delete(1) }
        verify(deviceRepository, never()).delete(any())
    }
}
