package com.back.homeapp.mqtt

data class SensorReadingMessage(
    val temperature: Double,
    val humidity: Double,
    val deviceId: Long,
)
