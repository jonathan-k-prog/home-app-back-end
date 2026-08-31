package com.back.homeapp.weather

import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.util.UriComponentsBuilder

@Service
class WeatherService(
    restClientBuilder: RestClient.Builder,
    private val weatherProperties: WeatherProperties,
) {
    private val restClient =
        restClientBuilder
            .baseUrl(weatherProperties.baseUrl)
            .build()

    fun fetch(): WeatherResponse {
        if (weatherProperties.apiKey.isBlank()) {
            throw IllegalStateException("weather.api-key is not configured")
        }

        val uri =
            UriComponentsBuilder
                .fromPath("/v1/current.json")
                .queryParam("key", weatherProperties.apiKey)
                .queryParam("q", weatherProperties.location)
                .queryParam("aqi", "no")
                .build()
                .toUriString()

        println(uri)
        try {
            val apiResponse =
                restClient
                    .get()
                    .uri(uri)
                    .retrieve()
                    .body(WeatherApiResponse::class.java)
                    ?: throw IllegalStateException("Weather API returned an empty body")
            return WeatherResponse(
                apiResponse.current.temp_c,
                apiResponse.current.wind_kph,
                apiResponse.current.wind_dir,
                apiResponse.current.wind_degree,
                apiResponse.current.precip_mm,
                apiResponse.current.pressure_in,
                apiResponse.current.chance_of_rain,
                apiResponse.current.chance_of_snow,
                apiResponse.current.uv,
                apiResponse.current.humidity,
                apiResponse.current.cloud,
                apiResponse.current.condition.text,
            )
        } catch (exception: RestClientResponseException) {
            throw IllegalStateException(
                "Weather API error: ${exception.statusCode.value()}",
                exception,
            )
        }
    }
}
