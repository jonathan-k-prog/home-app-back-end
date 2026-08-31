package com.back.homeapp.humidityReport

import com.back.homeapp.device.Device
import com.back.homeapp.device.DeviceRepository
import com.back.homeapp.home.Home
import com.back.homeapp.room.Room
import com.back.homeapp.roomType.RoomType
import com.back.homeapp.user.User
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.time.Instant
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class HumidityReportServiceTest {
    @Mock
    private lateinit var humidityReportRepository: HumidityReportRepository

    @Mock
    private lateinit var deviceRepository: DeviceRepository

    private lateinit var humidityReportService: HumidityReportService

    private val user = User(id = 1, email = "plouf@example.com", googleId = "g-1", name = "Plouf", timestamp = Instant.now())
    private val home = Home(id = 1, name = "Maison", identifier = "home-1", timestamp = Instant.now(), creator = user)
    private val room = Room(id = 1, name = "Salon", type = RoomType.DEFAULT, home = home)
    private val device = Device(id = 1, name = "DHT11", identifier = "esp32-1", room = room)

    @BeforeEach
    fun setUp() {
        humidityReportService = HumidityReportService(humidityReportRepository, deviceRepository)
    }

    @Test
    fun `findAll returns reports sorted by timestamp descending`() {
        val older = HumidityReport(id = 1, value = 40.0, timestamp = Instant.ofEpochMilli(1000), device = device)
        val newer = HumidityReport(id = 2, value = 45.0, timestamp = Instant.ofEpochMilli(2000), device = device)
        whenever(humidityReportRepository.findAll()).thenReturn(listOf(older, newer))

        val result = humidityReportService.findAll()

        assertEquals(listOf(2L, 1L), result.map { it.id })
    }

    @Test
    fun `create saves a report when the device exists`() {
        whenever(deviceRepository.findById(1)).thenReturn(Optional.of(device))
        whenever(humidityReportRepository.save(any())).thenAnswer { it.arguments[0] as HumidityReport }

        val result =
            humidityReportService.create(
                HumidityReportRequest(value = 55.0, timestamp = Instant.now(), deviceId = 1),
            )

        assertEquals(55.0, result.value)
    }

    @Test
    fun `create throws when the device does not exist`() {
        whenever(deviceRepository.findById(99)).thenReturn(Optional.empty())

        assertThrows(IllegalArgumentException::class.java) {
            humidityReportService.create(
                HumidityReportRequest(value = 55.0, timestamp = Instant.now(), deviceId = 99),
            )
        }
    }
}
