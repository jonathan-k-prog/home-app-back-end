package com.back.homeapp.humidityReport

import com.back.homeapp.device.Device
import java.time.Instant

data class HumidityReportResponse(
    val id: Long?,
    val value: Double,
    val timestamp: Long,
)
