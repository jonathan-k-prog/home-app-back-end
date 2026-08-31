package com.back.homeapp.temperatureReport

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
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class TemperatureReportServiceTest {
    @Mock
    private lateinit var temperatureReportRepository: TemperatureReportRepository

    @Mock
    private lateinit var deviceRepository: DeviceRepository

    private lateinit var temperatureReportService: TemperatureReportService

    private val user = User(id = 1, email = "plouf@example.com", googleId = "g-1", name = "Plouf", timestamp = Instant.now())
    private val home = Home(id = 1, name = "Maison", identifier = "home-1", timestamp = Instant.now(), creator = user)
    private val room = Room(id = 1, name = "Salon", type = RoomType.DEFAULT, home = home)
    private val device = Device(id = 1, name = "DHT11", identifier = "esp32-1", room = room)

    @BeforeEach
    fun setUp() {
        temperatureReportService = TemperatureReportService(temperatureReportRepository, deviceRepository)
    }

    @Test
    fun `findAll returns reports sorted by timestamp descending`() {
        val older = TemperatureReport(id = 1, value = 18.0, timestamp = Instant.ofEpochMilli(1000), device = device)
        val newer = TemperatureReport(id = 2, value = 21.0, timestamp = Instant.ofEpochMilli(2000), device = device)
        val pageable = PageRequest.of(0, 10)
        whenever(temperatureReportRepository.findAllByOrderByTimestampDesc(pageable))
            .thenReturn(PageImpl(listOf(older, newer)))

        val result = temperatureReportService.findAll(pageable)

        assertEquals(listOf(2L, 1L), result.map { it.id })
    }

    @Test
    fun `findLatestByDeviceId returns the latest report`() {
        val report = TemperatureReport(id = 1, value = 21.0, timestamp = Instant.now(), device = device)
        whenever(temperatureReportRepository.findTopByDeviceIdOrderByTimestampDesc(1)).thenReturn(report)

        val result = temperatureReportService.findLatestByDeviceId(1)

        assertEquals(21.0, result.value)
    }

    @Test
    fun `findLatestByDeviceId throws 404 when there is no report`() {
        whenever(temperatureReportRepository.findTopByDeviceIdOrderByTimestampDesc(1)).thenReturn(null)

        assertThrows(ResponseStatusException::class.java) { temperatureReportService.findLatestByDeviceId(1) }
    }

    @Test
    fun `create saves a report when the device exists`() {
        whenever(deviceRepository.findById(1)).thenReturn(Optional.of(device))
        whenever(temperatureReportRepository.save(any())).thenAnswer { it.arguments[0] as TemperatureReport }

        val result =
            temperatureReportService.create(
                TemperatureReportRequest(value = 22.0, timestamp = Instant.now(), deviceId = 1),
            )

        assertEquals(22.0, result.value)
    }

    @Test
    fun `create throws when the device does not exist`() {
        whenever(deviceRepository.findById(99)).thenReturn(Optional.empty())

        assertThrows(IllegalArgumentException::class.java) {
            temperatureReportService.create(
                TemperatureReportRequest(value = 22.0, timestamp = Instant.now(), deviceId = 99),
            )
        }
    }
}
