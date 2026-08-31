package com.back.homeapp.temperatureReport

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.time.Instant

data class TemperatureReportRequest(
    @field:NotNull(message = "Temperature value is required")
    @field:Min(value = -50, message = "Temperature must be at least -50")
    @field:Max(value = 100, message = "Temperature must be at most 100")
    var value: Double,
    @field:NotNull(message = "Timestamp is required")
    var timestamp: Instant,
    @field:NotNull(message = "Device id is required")
    @field:Min(value = 1, message = "Device id must be greater than 0")
    var deviceId: Long,
)
