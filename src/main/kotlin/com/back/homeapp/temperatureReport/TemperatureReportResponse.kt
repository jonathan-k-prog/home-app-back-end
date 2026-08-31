package com.back.homeapp.temperatureReport

data class TemperatureReportResponse(
    val id: Long?,
    val value: Double,
    val timestamp: Long,
)
