package com.back.homeapp.weather

data class WeatherResponse(
    val temperature: Float,
    val windSpeed: Long,
    val windDirection: String,
    val windDirectionDegree: Long,
    val precipitation: Float,
    val pressure: Float,
    val rainChance: Int,
    val snowChance: Int,
    val uv: Float,
    val humidity: Float,
    val cloud: Int,
    val condition: String,
)
