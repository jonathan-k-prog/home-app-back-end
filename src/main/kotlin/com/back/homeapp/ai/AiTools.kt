package com.back.homeapp.ai

import com.back.homeapp.device.DeviceResponse
import com.back.homeapp.device.DeviceService
import com.back.homeapp.room.RoomResponse
import com.back.homeapp.room.RoomService
import org.springframework.ai.tool.annotation.Tool
import org.springframework.stereotype.Component

@Component
class AiTools(
    private val deviceService: DeviceService,
    private val roomService: RoomService,
) {
    @Tool(description = "Get the list of all rooms in the home with their type (e.g. bedroom, living room, kitchen).")
    fun getAllRooms(): List<RoomResponse> = roomService.findAll(null)

    @Tool(
        description =
            "Get the list of all smart devices in the home with their name, type, connection status, " +
                "last seen timestamp, and their room. Also includes the latest temperature and humidity " +
                "readings for each device.",
    )
    fun getAllDevices(): List<DeviceResponse> = deviceService.findAll()

    @Tool(description = "Get all devices in a specific room by room ID, including their status and sensor readings.")
    fun getDevicesByRoom(roomId: Long): List<DeviceResponse> = deviceService.findAll(null, roomId)
}
