package com.back.homeapp.mqtt

import com.back.homeapp.device.DeviceResponse
import com.back.homeapp.device.DeviceService
import com.back.homeapp.deviceType.DeviceType
import com.back.homeapp.home.HomeResponse
import com.back.homeapp.humidityReport.HumidityReportRequest
import com.back.homeapp.humidityReport.HumidityReportResponse
import com.back.homeapp.humidityReport.HumidityReportService
import com.back.homeapp.room.RoomResponse
import com.back.homeapp.roomType.RoomType
import com.back.homeapp.temperatureReport.TemperatureReportRequest
import com.back.homeapp.temperatureReport.TemperatureReportResponse
import com.back.homeapp.temperatureReport.TemperatureReportService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import java.lang.reflect.Method
import java.time.Instant

@ExtendWith(MockitoExtension::class)
class MqttSubscriberServiceTest {
    @Mock
    private lateinit var humidityReportService: HumidityReportService

    @Mock
    private lateinit var temperatureReportService: TemperatureReportService

    @Mock
    private lateinit var deviceService: DeviceService

    private lateinit var service: MqttSubscriberService
    private lateinit var handleMessage: Method

    private val homeResponse = HomeResponse(id = 1, name = "Maison", identifier = "home-1", timestamp = Instant.now())
    private val roomResponse =
        RoomResponse(id = 1, name = "Salon", width = 10, height = 10, x = 0, y = 0, floor = 0, type = RoomType.DEFAULT, home = homeResponse)
    private val deviceResponse =
        DeviceResponse(
            id = 1,
            name = "DHT11",
            identifier = "esp32-1",
            connected = false,
            lastSeen = Instant.now(),
            type = DeviceType.ESP_32_DHT11,
            room = roomResponse,
            averageTemperature = 0.0,
            minTemperatureReport = null,
            maxTemperatureReport = null,
            lastTemperatureReport = null,
            averageHumidity = 0.0,
            minHumidityReport = null,
            maxHumidityReport = null,
            lastHumidityReport = null,
            alertThresholds = emptyList(),
        )

    @BeforeEach
    fun setUp() {
        val properties = MqttProperties()
        val objectMapper = jacksonObjectMapper()
        service = MqttSubscriberService(properties, objectMapper, humidityReportService, temperatureReportService, deviceService)
        handleMessage = MqttSubscriberService::class.java.getDeclaredMethod("handleMessage", String::class.java, String::class.java)
        handleMessage.isAccessible = true
    }

    private fun invokeHandleMessage(
        topic: String,
        payload: String,
    ) {
        handleMessage.invoke(service, topic, payload)
    }

    @Test
    fun `handleMessage records readings and marks the device connected`() {
        whenever(deviceService.findByIdentifierAndHomeIdentifier("esp32-1", "home-1")).thenReturn(deviceResponse)
        whenever(deviceService.markConnected(eq(1L), any())).thenReturn(deviceResponse)
        whenever(humidityReportService.create(any())).thenReturn(
            HumidityReportResponse(id = 1, value = 50.0, timestamp = Instant.now().toEpochMilli()),
        )
        whenever(temperatureReportService.create(any())).thenReturn(
            TemperatureReportResponse(id = 1, value = 21.0, timestamp = Instant.now().toEpochMilli()),
        )

        val payload =
            """{"temperature":21.0,"humidity":50.0,"deviceIdentifier":"esp32-1","homeIdentifier":"home-1"}"""

        invokeHandleMessage("esp32/dht11", payload)

        verify(deviceService).markConnected(eq(1L), any())
        verify(humidityReportService).create(any<HumidityReportRequest>())
        verify(temperatureReportService).create(any<TemperatureReportRequest>())
    }

    @Test
    fun `handleMessage swallows errors for malformed payloads`() {
        assertDoesNotThrow { invokeHandleMessage("esp32/dht11", "not-json") }

        verify(deviceService, never()).markConnected(any(), any())
        verify(humidityReportService, never()).create(any())
        verify(temperatureReportService, never()).create(any())
    }

    @Test
    fun `handleMessage swallows errors when the device is unknown`() {
        whenever(deviceService.findByIdentifierAndHomeIdentifier("missing", "home-1"))
            .thenThrow(RuntimeException("Device missing for home home-1 not found"))

        val payload =
            """{"temperature":21.0,"humidity":50.0,"deviceIdentifier":"missing","homeIdentifier":"home-1"}"""

        assertDoesNotThrow { invokeHandleMessage("esp32/dht11", payload) }

        verify(humidityReportService, never()).create(any())
        verify(temperatureReportService, never()).create(any())
    }
}
