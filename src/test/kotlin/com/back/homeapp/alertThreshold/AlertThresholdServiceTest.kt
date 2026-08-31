package com.back.homeapp.alertThreshold

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
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class AlertThresholdServiceTest {
    @Mock
    private lateinit var alertThresholdRepository: AlertThresholdRepository

    @Mock
    private lateinit var deviceRepository: DeviceRepository

    private lateinit var alertThresholdService: AlertThresholdService

    private val user = User(id = 1, email = "plouf@example.com", googleId = "g-1", name = "Plouf", timestamp = Instant.now())
    private val home = Home(id = 1, name = "Maison", identifier = "home-1", timestamp = Instant.now(), creator = user)
    private val room = Room(id = 1, name = "Salon", type = RoomType.DEFAULT, home = home)
    private val device = Device(id = 1, name = "DHT11", identifier = "esp32-1", room = room)

    @BeforeEach
    fun setUp() {
        alertThresholdService = AlertThresholdService(alertThresholdRepository, deviceRepository)
    }

    private fun request(deviceId: Long = 1) =
        AlertThresholdRequest(metric = AlertThresholdMetric.TEMPERATURE, minValue = 10.0, maxValue = 30.0, deviceId = deviceId)

    @Test
    fun `create saves a threshold when the device exists`() {
        whenever(deviceRepository.findById(1)).thenReturn(Optional.of(device))
        whenever(alertThresholdRepository.save(any())).thenAnswer { it.arguments[0] as AlertThreshold }

        val result = alertThresholdService.create(request())

        assertEquals(AlertThresholdMetric.TEMPERATURE, result.metric)
        assertEquals(10.0, result.minValue)
    }

    @Test
    fun `create throws 404 when the device does not exist`() {
        whenever(deviceRepository.findById(99)).thenReturn(Optional.empty())

        assertThrows(ResponseStatusException::class.java) { alertThresholdService.create(request(deviceId = 99)) }
        verify(alertThresholdRepository, never()).save(any())
    }

    @Test
    fun `findAllByDeviceId maps every threshold for the device`() {
        val threshold = AlertThreshold(id = 1, metric = AlertThresholdMetric.HUMIDITY, device = device)
        whenever(alertThresholdRepository.findAllByDeviceId(1)).thenReturn(listOf(threshold))

        val result = alertThresholdService.findAllByDeviceId(1)

        assertEquals(1, result.size)
        assertEquals(AlertThresholdMetric.HUMIDITY, result.first().metric)
    }

    @Test
    fun `update modifies and saves an existing threshold`() {
        val threshold = AlertThreshold(id = 1, metric = AlertThresholdMetric.TEMPERATURE, device = device)
        whenever(alertThresholdRepository.findById(1)).thenReturn(Optional.of(threshold))
        whenever(deviceRepository.findById(1)).thenReturn(Optional.of(device))
        whenever(alertThresholdRepository.save(any())).thenAnswer { it.arguments[0] as AlertThreshold }

        val result = alertThresholdService.update(1, request().copy(maxValue = 35.0))

        assertEquals(35.0, result.maxValue)
    }

    @Test
    fun `update throws 404 when the threshold is missing`() {
        whenever(alertThresholdRepository.findById(1)).thenReturn(Optional.empty())

        assertThrows(ResponseStatusException::class.java) { alertThresholdService.update(1, request()) }
        verify(deviceRepository, never()).findById(any())
    }

    @Test
    fun `update throws 404 when the device is missing`() {
        val threshold = AlertThreshold(id = 1, metric = AlertThresholdMetric.TEMPERATURE, device = device)
        whenever(alertThresholdRepository.findById(1)).thenReturn(Optional.of(threshold))
        whenever(deviceRepository.findById(99)).thenReturn(Optional.empty())

        assertThrows(ResponseStatusException::class.java) { alertThresholdService.update(1, request(deviceId = 99)) }
        verify(alertThresholdRepository, never()).save(any())
    }

    @Test
    fun `delete removes an existing threshold`() {
        val threshold = AlertThreshold(id = 1, metric = AlertThresholdMetric.TEMPERATURE, device = device)
        whenever(alertThresholdRepository.findById(1)).thenReturn(Optional.of(threshold))

        val result = alertThresholdService.delete(1)

        assertEquals(1L, result.id)
        verify(alertThresholdRepository).delete(threshold)
    }

    @Test
    fun `delete throws 404 when missing`() {
        whenever(alertThresholdRepository.findById(1)).thenReturn(Optional.empty())

        assertThrows(ResponseStatusException::class.java) { alertThresholdService.delete(1) }
        verify(alertThresholdRepository, never()).delete(any())
    }
}
