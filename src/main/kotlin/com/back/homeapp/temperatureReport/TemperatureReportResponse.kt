package com.back.homeapp.temperatureReport

import com.back.homeapp.device.Device
import java.time.Instant

data class TemperatureReportResponse(
    val id: Long?,
    val value: Double,
    val timestamp: Long,
)
