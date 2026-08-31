package com.back.homeapp.weather

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "weather")
data class WeatherProperties(
    val apiKey: String = "",
    val baseUrl: String = "",
    val location: String = "",
)
