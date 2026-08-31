package com.back.homeapp.device

import com.back.homeapp.alertThreshold.AlertThresholdResponse
import com.back.homeapp.deviceType.DeviceType
import com.back.homeapp.humidityReport.HumidityReportResponse
import com.back.homeapp.room.RoomResponse
import com.back.homeapp.temperatureReport.TemperatureReportResponse
import java.time.Instant

data class DeviceResponse(
    val id: Long?,
    val name: String,
    val identifier: String,
    val connected: Boolean,
    val lastSeen: Instant,
    val type: DeviceType,
    val room: RoomResponse,
    val averageTemperature: Double,
    val minTemperatureReport: TemperatureReportResponse?,
    val maxTemperatureReport: TemperatureReportResponse?,
    val lastTemperatureReport: TemperatureReportResponse?,
    val averageHumidity: Double,
    val minHumidityReport: HumidityReportResponse?,
    val maxHumidityReport: HumidityReportResponse?,
    val lastHumidityReport: HumidityReportResponse?,
    val alertThresholds: List<AlertThresholdResponse>,
)
