package com.back.homeapp.device

import com.back.homeapp.deviceType.DeviceType
import com.back.homeapp.humidityReport.HumidityReportResponse
import com.back.homeapp.room.RoomResponse
import com.back.homeapp.temperatureReport.TemperatureReportResponse

data class DeviceResponse(
    val id: Long?,
    val name: String,
    val type: DeviceType,
    val room: RoomResponse,
    val humidityReports: List<HumidityReportResponse>,
    val temperatureReports: List<TemperatureReportResponse>,
)
