package com.back.homeapp.command

import com.back.homeapp.device.Device
import com.back.homeapp.device.DeviceRepository
import com.back.homeapp.home.Home
import com.back.homeapp.mqtt.MqttPublisherService
import com.back.homeapp.room.Room
import com.back.homeapp.room.RoomRepository
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
import org.mockito.kotlin.whenever
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class CommandServiceTest {
    @Mock
    private lateinit var mqttPublisherService: MqttPublisherService

    @Mock
    private lateinit var deviceRepository: DeviceRepository

    @Mock
    private lateinit var roomRepository: RoomRepository

    private val user = User(id = 1, email = "plouf@example.com", googleId = "g-1", name = "Plouf", timestamp = Instant.now())
    private val home = Home(id = 1, name = "Maison", identifier = "home-1", timestamp = Instant.now(), creator = user)
    private val room = Room(id = 1, name = "Salon", type = RoomType.DEFAULT, home = home)
    private val device = Device(id = 1, name = "DHT11", identifier = "esp32-1", room = room)

    @Test
    fun `send publishes a command to a device topic`() {
        val commandService = CommandService(mqttPublisherService, deviceRepository, roomRepository)
        whenever(deviceRepository.findById(1)).thenReturn(Optional.of(device))

        val result = commandService.send(CommandRequest(targetType = CommandTargetType.DEVICE, targetId = 1, action = "ON"))

        assertEquals("Command 'ON' sent to device 1", result)
        verify(mqttPublisherService).publish("esp32/dht11/command/1", "ON")
    }

    @Test
    fun `send publishes a command to a room topic`() {
        val commandService = CommandService(mqttPublisherService, deviceRepository, roomRepository)
        whenever(roomRepository.findById(1)).thenReturn(Optional.of(room))

        val result = commandService.send(CommandRequest(targetType = CommandTargetType.ROOM, targetId = 1, action = "OFF"))

        assertEquals("Command 'OFF' sent to room 1", result)
        verify(mqttPublisherService).publish("esp32/commands/room/1", "OFF")
    }

    @Test
    fun `send throws 404 when the device does not exist`() {
        val commandService = CommandService(mqttPublisherService, deviceRepository, roomRepository)
        whenever(deviceRepository.findById(99)).thenReturn(Optional.empty())

        assertThrows(ResponseStatusException::class.java) {
            commandService.send(CommandRequest(targetType = CommandTargetType.DEVICE, targetId = 99, action = "ON"))
        }
        verify(mqttPublisherService, never()).publish(org.mockito.kotlin.any(), org.mockito.kotlin.any(), org.mockito.kotlin.any())
    }

    @Test
    fun `send throws 404 when the room does not exist`() {
        val commandService = CommandService(mqttPublisherService, deviceRepository, roomRepository)
        whenever(roomRepository.findById(99)).thenReturn(Optional.empty())

        assertThrows(ResponseStatusException::class.java) {
            commandService.send(CommandRequest(targetType = CommandTargetType.ROOM, targetId = 99, action = "OFF"))
        }
    }

    @Test
    fun `send works without a configured mqtt publisher`() {
        val commandService = CommandService(null, deviceRepository, roomRepository)
        whenever(deviceRepository.findById(1)).thenReturn(Optional.of(device))

        val result = commandService.send(CommandRequest(targetType = CommandTargetType.DEVICE, targetId = 1, action = "ON"))

        assertEquals("Command 'ON' sent to device 1", result)
    }
}
